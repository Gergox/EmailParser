package com.example.kybl.emailparser;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

public class LinkAndEmailCallable implements Callable<LinkAnEmailHolder> {
    private String link;

    public LinkAndEmailCallable(String link) {
        this.link = link;
    }

    @Override
    public LinkAnEmailHolder call() {
        String htmlPage = "";
        try {
            htmlPage = new HtmlPageLoader(new URL(link)).getHTMLFromWebPage();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        LinkAndEmailExtractor linkAndEmailExtractor = new LinkAndEmailExtractor(htmlPage);
        LinkAnEmailHolder holder = new LinkAnEmailHolder();
        holder.setEmails(linkAndEmailExtractor.grabEmails());
        holder.setLinks(linkAndEmailExtractor.grabLinks());
        return holder;

    }
}
