package com.branches.utils;

import com.branches.exception.InternalServerError;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class HtmlToPdfConverter {
    public byte[] execute(String htmlContent) {
        try (Playwright playwright = Playwright.create()) {

            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(java.util.Arrays.asList(
                            "--disable-dev-shm-usage",
                            "--disable-setuid-sandbox",
                            "--no-sandbox",
                            "--disable-gpu",
                            "--disable-software-rasterizer",
                            "--disable-extensions",
                            "--no-first-run",
                            "--no-zygote",
                            "--single-process"
                    ));

            Browser browser = playwright.chromium().launch(launchOptions);
            BrowserContext context = browser.newContext();
            Page page = context.newPage();

            page.navigate("about:blank");
            page.setContent(htmlContent);

            page.waitForLoadState(LoadState.NETWORKIDLE);

            Page.PdfOptions pdfOptions = new Page.PdfOptions()
                    .setFormat("A4")
                    .setPrintBackground(true);

            byte[] pdfBytes = page.pdf(pdfOptions);

            browser.close();

            return pdfBytes;

        } catch (Exception e) {
            log.error("Erro ao gerar PDF a partir do HTML: ", e);
            throw new InternalServerError("Erro ao gerar PDF: " + e.getMessage());
        }
    }
}
