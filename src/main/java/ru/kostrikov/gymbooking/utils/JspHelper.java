package ru.kostrikov.gymbooking.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.net.URI;
import java.util.Optional;

@UtilityClass
public class JspHelper {

    private static final int PAGE_SIZE = 2;

    public static int getPageByRequestParameter(String page, int def) {
        return Optional.ofNullable(page).filter(s -> !s.isEmpty()).map(Integer::valueOf).orElseGet(() -> def);
    }

    @SneakyThrows
    public static String getRefererPath(String referer) {
        URI refererUri = new URI(referer);
        return refererUri.getPath();
    }

    private final static String JSP_FORMAT = "/WEB-INF/jsp/%s.jsp";

    public String getPath(String jsp) {
        return JSP_FORMAT.formatted(jsp);
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }
}
