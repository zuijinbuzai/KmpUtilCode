package io.github.zuijinbuzai.kmputilcode.codec
import kotlin.test.*
class CodecTest {
    @Test fun knownDigestVectors() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",Codec.sha256("abc".encodeToByteArray()))
        assertEquals("f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8",Codec.hmacSha256("The quick brown fox jumps over the lazy dog".encodeToByteArray(),"key".encodeToByteArray()))
    }
    @Test fun unicodeRoundTrip() {
        val text="你好 👋 + /?&=%"
        assertEquals(text,Codec.urlDecode(Codec.urlEncode(text)))
        assertEquals("a+b",Codec.urlDecode("a+b"))
        assertEquals("a b",Codec.urlDecode("a+b",true))
        assertEquals(text,Codec.decodeBase64(Codec.base64(text.encodeToByteArray()))!!.decodeToString())
        assertEquals("000fff",Codec.hex(byteArrayOf(0,15,-1)))
        assertContentEquals(byteArrayOf(0,15,-1),Codec.decodeHex("000fff"))
    }
    @Test fun malformedInput() {
        assertFailsWith<IllegalArgumentException> { Codec.urlDecode("%GG") }
        assertFailsWith<IllegalArgumentException> { Codec.urlDecode("%A") }
        assertFailsWith<CharacterCodingException> { Codec.urlDecode("%FF") }
        assertNull(Codec.decodeBase64("!!"))
        assertFailsWith<IllegalArgumentException> { Codec.decodeHex("a") }
    }
}
