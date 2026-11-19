import SwiftUI
import DemoKit

@main
struct KmpUtilCodeDemoApp: App {
    var body: some Scene { WindowGroup { CatalogView() } }
}

struct CatalogView: View {
    @State private var query = ""
    private let samples = DemoCatalog.shared.cases
    var body: some View {
        NavigationView {
            List {
                Section {
                    Text("一次封装，跨端复用").font(.headline)
                    Text("9 个能力模块 · 17 组示例\nAndroid / iOS / JVM").foregroundColor(.secondary)
                }
                ForEach(samples.filter {
                    query.isEmpty || ($0.title + $0.category + $0.description_).localizedCaseInsensitiveContains(query)
                }, id: \.id) { sample in
                    NavigationLink(destination: SampleView(sample: sample)) {
                        VStack(alignment: .leading, spacing: 6) {
                            Text(sample.category).font(.caption).foregroundColor(.teal)
                            Text(sample.title).font(.headline)
                            Text(sample.description_).font(.caption).foregroundColor(.secondary)
                        }.padding(.vertical, 6)
                    }
                }
            }
            .navigationTitle("KmpUtilCode")
            .searchable(text: $query, prompt: "搜索工具或场景")
        }.navigationViewStyle(.stack)
    }
}

struct SampleView: View {
    let sample: DemoCase
    @State private var output = "点击运行查看结果。"
    @State private var running = false
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 18) {
                Text(sample.description_).foregroundColor(.secondary)
                Text(sample.code).font(.system(.caption, design: .monospaced)).textSelection(.enabled)
                    .padding().frame(maxWidth: .infinity, alignment: .leading).background(Color.secondary.opacity(0.08)).cornerRadius(12)
                Button(running ? "运行中…" : "运行示例") { run() }.buttonStyle(.borderedProminent).disabled(running)
                Text(output).font(.system(.body, design: .monospaced)).textSelection(.enabled)
            }.padding()
        }.navigationTitle(sample.title).tint(.teal)
    }
    private func run() {
        running = true
        output = "运行中…"
        // File/preferences samples perform blocking IO, so start off the UI thread.
        let root = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask)[0]
            .appendingPathComponent("KmpUtilCodeDemo").path
        DispatchQueue.global(qos: .userInitiated).async {
            DemoCatalog.shared.run(id: sample.id, platformContext: nil, fileRoot: root) { value, error in
                DispatchQueue.main.async {
                    output = value ?? "运行失败：\(error?.localizedDescription ?? "unknown")"
                    running = false
                }
            }
        }
    }
}
