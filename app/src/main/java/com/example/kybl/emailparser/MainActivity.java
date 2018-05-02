package com.example.kybl.emailparser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainActivity extends Activity {

    Button findBtn;
    TextView emailsTxt;
    ProgressBar progressBar;
    EditText txtUrl;
    Spinner spinner;
    private WebPageLoader webPageLoader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findBtn = findViewById(R.id.btnFind);
        emailsTxt = findViewById(R.id.emailsTxt);
        progressBar = findViewById(R.id.progressBar);
        txtUrl = findViewById(R.id.editTextURL);
        spinner = findViewById(R.id.spinner);

        emailsTxt.setMovementMethod(new ScrollingMovementMethod());
        progressBar.setVisibility(View.GONE);

        findBtn.setOnClickListener(view -> {
            Pattern urlPattern = Pattern.compile(LinkAndEmailExtractor.LINK_PATTERN);
            Matcher matcher = urlPattern.matcher(txtUrl.getText().toString());

            if (txtUrl.getText().toString().equals("")) {
                txtUrl.setError("must not be null");
            } else if (!matcher.find()) {
                txtUrl.setError("url not valid");
            } else {
                progressBar.setVisibility(View.VISIBLE);
                UserInput userInput = new UserInput();
                userInput.setUrl(txtUrl.getText().toString());
                userInput.setDeepLevel(spinner.getSelectedItem().toString());
                webPageLoader = (WebPageLoader) new WebPageLoader().execute(userInput);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webPageLoader != null) {
            webPageLoader.cancel(true);
        }
    }


    private class WebPageLoader extends AsyncTask<UserInput, Integer, Void> {

        Set<String> emails = new LinkedHashSet<>();

        @Override
        protected Void doInBackground(UserInput... values) {

            String url = values[0].getUrl();
            int deepLevel = values[0].getDeepLevel();
            emails.addAll(getAllEmailsFromWebPage(url, deepLevel));
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (emails.size() == 0) {
                emailsTxt.append("no emails");
                emailsTxt.append("\n");
            } else {
                for (String s : emails) {
                    emailsTxt.append(s);
                    emailsTxt.append("\n");
                }
            }
            progressBar.setVisibility(View.GONE);
        }

        private Set<String> getAllEmailsFromWebPage(String link, int deepLevel) {
            Set<String> links = new LinkedHashSet<>();
            links.add(link);

            String htmlPage = "";
            try {
                htmlPage = new HtmlPageLoader(new URL(link)).getHTMLFromWebPage();
            } catch (IOException e) {
                //e.printStackTrace();
            }
            LinkAndEmailExtractor linkAndEmailExtractor = new LinkAndEmailExtractor(htmlPage);
            Set<String> emails = linkAndEmailExtractor.grabEmails();
            Set<String> new_links = new LinkedHashSet<>();
            if (deepLevel > 1) {
                new_links = linkAndEmailExtractor.grabLinks();
            }
            while (deepLevel > 1) {
                new_links.removeAll(links);
                links.addAll(new_links);
                List<LinkAndEmailCallable> callableSet = new LinkedList<>();

                for (String newLink : new_links) {
                    callableSet.add(new LinkAndEmailCallable(newLink));
                }

                ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 2);

                LinkAnEmailHolder invoke = pool.invoke(new RecursiveFinder(callableSet));

                emails.addAll(invoke.getEmails());
                if (deepLevel > 2) {
                    new_links.addAll(invoke.getLinks());
                }
                --deepLevel;
            }


            Set<String> result = new LinkedHashSet<>();
            for (Iterator<String> iterator = emails.iterator(); iterator.hasNext(); ) {
                String email = iterator.next();
                if (!email.endsWith(".png")) {
                    result.add(email);
                }
            }
            return result;
        }
    }
}
