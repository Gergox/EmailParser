package com.example.kybl.emailparser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LinkAndEmailExtractor {

    public static final String LINK_PATTERN = "(http://|https://)(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]+.[a-z]{3}.?([a-z]+)?";
    private static final String EMAIL_PATTERN = "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";

    private Pattern patternLink, patternEmail;
    private String webPage;

    public LinkAndEmailExtractor(String webPage) {
        patternEmail = Pattern.compile(EMAIL_PATTERN);
        patternLink = Pattern.compile(LINK_PATTERN);
        this.webPage = webPage;
    }

    private Set<String> grab(Pattern pattern) {
        Set<String> temp = new LinkedHashSet<>();
        Matcher matcher = pattern.matcher(webPage);
        while (matcher.find()) {
            temp.add(matcher.group());
        }
        return temp;
    }

    public Set<String> grabEmails() {
        Set<String> result = new LinkedHashSet<>();

        for (Iterator<String> i = grab(patternEmail).iterator(); i.hasNext(); ) {
            String email = i.next();
            if (!email.endsWith(".png") | (!email.endsWith(".jpg"))) {
                result.add(email);
            }
        }
        return result;
    }

    public Set<String> grabLinks() {
        Set<String> result = new LinkedHashSet<>();

        for (Iterator<String> iterator = grab(patternLink).iterator(); iterator.hasNext(); ) {
            String link = iterator.next();
            result.add(link.replaceAll("[\"<>]", ""));
        }
        return result;
    }
}
