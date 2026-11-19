package io.github.zuijinbuzai.kmputilcode.demo.android

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.*
import android.text.Editable
import android.text.TextWatcher
import io.github.zuijinbuzai.kmputilcode.demo.*
import io.github.zuijinbuzai.kmputilcode.platform.AndroidTools
import kotlinx.coroutines.*

class MainActivity : Activity() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val ink = Color.rgb(26, 43, 52)
    private val teal = Color.rgb(0, 115, 107)
    private lateinit var cards: LinearLayout
    private var category = "全部"
    private var query = ""
    private fun dp(value: Int) = AndroidTools.dpToPx(this, value.toFloat())
    private fun text(value: String, size: Float = 15f, color: Int = ink) = TextView(this).apply {
        text = value; textSize = size; setTextColor(color); setPadding(0, dp(5), 0, dp(5))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(241, 246, 244)
        window.navigationBarColor = Color.rgb(241, 246, 244)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.rgb(241, 246, 244))
            setPadding(dp(20), dp(12), dp(20), 0)
            setOnApplyWindowInsetsListener { view, insets ->
                view.setPadding(dp(20), insets.systemWindowInsetTop + dp(12), dp(20), insets.systemWindowInsetBottom)
                insets
            }
        }
        root.addView(text("KMP / ENGINEERING TOOLKIT", 11f, teal).apply { letterSpacing = 0.15f })
        root.addView(text("KmpUtilCode", 32f).apply { setTypeface(null, Typeface.BOLD) })
        root.addView(text("一次封装，跨端复用。\n探索通用组件，查看源码，运行真实示例。", 15f))
        root.addView(text("9 个能力模块  ·  ${DemoCatalog.cases.size} 组示例  ·  Android / iOS / JVM", 12f, teal))
        val search = EditText(this).apply { hint = "搜索工具、场景或关键词"; isSingleLine = true; textSize = 15f }
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { query = s.toString(); render() }
            override fun afterTextChanged(s: Editable?) = Unit
        })
        root.addView(search)
        val tabs = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        for (name in listOf("全部") + DemoCatalog.cases.map { it.category }.distinct()) {
            tabs.addView(Button(this).apply { text = name; textSize = 12f; isAllCaps = false; setOnClickListener { category = name; render() } })
        }
        root.addView(HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled = false; addView(tabs) })
        cards = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(ScrollView(this).apply { addView(cards) }, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
        render()
    }
    private fun render() {
        if (!::cards.isInitialized) return
        cards.removeAllViews()
        val samples = DemoCatalog.cases.filter {
            (category == "全部" || it.category == category) &&
                (it.title + it.description + it.category + it.code).contains(query, ignoreCase = true)
        }
        cards.addView(text(getString(R.string.sample_count, samples.size), 12f, teal))
        for (sample in samples) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL; setPadding(dp(16), dp(12), dp(16), dp(12))
                background = android.graphics.drawable.GradientDrawable().apply { setColor(Color.WHITE); cornerRadius = dp(14).toFloat() }
                layoutParams = LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(10) }
                contentDescription = "${sample.title}，点击查看示例"
                isFocusable = true
                setOnClickListener { showSample(sample) }
            }
            card.addView(text(sample.category, 11f, teal))
            card.addView(text(sample.title, 19f).apply { setTypeface(null, Typeface.BOLD) })
            card.addView(text(sample.description, 13f, Color.DKGRAY))
            card.addView(text("查看代码与运行结果  →", 12f, teal))
            cards.addView(card)
        }
        if (samples.isEmpty()) cards.addView(text("没有匹配结果，请尝试其他关键词。"))
    }
    private fun showSample(sample: DemoCase) {
        val panel = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), dp(12)) }
        panel.addView(text(sample.description, 14f))
        panel.addView(text(sample.code, 12f).apply { typeface = Typeface.MONOSPACE; setTextIsSelectable(true) })
        val output = text("点击运行查看结果。", 14f)
        output.setTextIsSelectable(true)
        panel.addView(output)
        var job: Job? = null
        val dialog = AlertDialog.Builder(this).setTitle(sample.title).setView(ScrollView(this).apply { addView(panel) })
            .setNegativeButton("关闭", null).setNeutralButton("复制代码") { _, _ -> AndroidTools.copyText(this, sample.code) }
            .setPositiveButton("运行", null).create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                button.isEnabled = false; output.text = "运行中…"
                job = scope.launch {
                    try {
                        output.text = withContext(Dispatchers.IO) { DemoCatalog.run(sample.id, applicationContext, filesDir.resolve("showcase").path) }
                    } catch (cancelled: CancellationException) { throw cancelled }
                    catch (failure: Exception) { output.text = getString(R.string.run_failed, failure.message ?: "unknown") }
                    finally { button.isEnabled = true }
                }
            }
        }
        dialog.setOnDismissListener { job?.cancel() }
        dialog.show()
    }
    override fun onDestroy() { scope.cancel(); super.onDestroy() }
}
