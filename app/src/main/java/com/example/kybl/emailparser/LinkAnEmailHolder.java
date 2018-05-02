package com.example.kybl.emailparser;

import java.util.LinkedHashSet;
import java.util.Set;

public class LinkAnEmailHolder {

    private Set<String> links = new LinkedHashSet<>();
    private Set<String> emails = new LinkedHashSet<>();

    public LinkAnEmailHolder(Set<String> links, Set<String> emails) {
        this.links = links;
        this.emails = emails;
    }

    public LinkAnEmailHolder() {
    }

    public Set<String> getLinks() {
        return links;
    }

    public void setLinks(Set<String> links) {
        this.links = links;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }
}
