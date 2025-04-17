package net.gidosa.common.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class HtmlSanitizerUtil {
    public static String customSanitize1(String rawHtml) {
        Safelist safelist = new Safelist()
                .addTags("p", "a", "img") // 허용할 태그
                .addAttributes("a", "href", "target") // <a> 태그에 허용할 속성
                .addAttributes("img", "src", "alt")   // <img> 태그에 허용할 속성
                .addProtocols("a", "href", "http", "https") // <a href="...">에 허용할 프로토콜
                .addProtocols("img", "src", "http", "https", "data"); // <img src="..."> 허용 프로토콜

        return Jsoup.clean(rawHtml, safelist);
    }
}
