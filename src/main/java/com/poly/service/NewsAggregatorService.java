package com.poly.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

@Service
public class NewsAggregatorService {

    private static final Logger log = Logger.getLogger(NewsAggregatorService.class.getName());

    private static final List<String[]> RSS_FEEDS = List.of(
        new String[]{"https://vnexpress.net/rss/suc-khoe.rss",          "VnExpress"},
        new String[]{"https://tuoitre.vn/rss/suc-khoe.rss",             "Tuổi Trẻ"},
        new String[]{"https://thanhnien.vn/rss/suc-khoe.rss",           "Thanh Niên"},
        new String[]{"https://suckhoedoisong.vn/rss/tin-tuc.rss",            "Sức Khỏe & Đời Sống"},
        new String[]{"https://nld.com.vn/rss/suc-khoe.rss",               "Người Lao Động"},
        new String[]{"https://dantri.com.vn/rss/suc-khoe.rss",            "Dân Trí"}
    );

    private final List<RssNewsItem> cachedNews = new CopyOnWriteArrayList<>();
    private long lastFetchTime = 0;
    private static final long CACHE_TTL_MS = 30 * 60 * 1000L; // 30 phút

    public List<RssNewsItem> getLatestNews(int limit) {
        // Nếu cache trống hoặc hết hạn, fetch ngay (không async để tránh race condition)
        if (cachedNews.isEmpty() || System.currentTimeMillis() - lastFetchTime > CACHE_TTL_MS) {
            refreshNews();
        }
        List<RssNewsItem> result = new ArrayList<>(cachedNews);
        return result.size() > limit ? result.subList(0, limit) : result;
    }

    @Scheduled(fixedDelay = 1800000, initialDelay = 5000)
    public void refreshNews() {
        List<RssNewsItem> items = new ArrayList<>();
        for (String[] feed : RSS_FEEDS) {
            try {
                items.addAll(fetchFeed(feed[0], feed[1]));
            } catch (Exception e) {
                log.warning("Lỗi khi lấy RSS từ " + feed[1] + ": " + e.getMessage());
            }
        }
        items.sort(Comparator.comparing(RssNewsItem::getPubDate, Comparator.nullsLast(Comparator.reverseOrder())));
        cachedNews.clear();
        cachedNews.addAll(items);
        lastFetchTime = System.currentTimeMillis();
    }

    private List<RssNewsItem> fetchFeed(String feedUrl, String sourceName) throws Exception {
        List<RssNewsItem> items = new ArrayList<>();
        HttpURLConnection conn = (HttpURLConnection) URI.create(feedUrl).toURL().openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(8000);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (MEDISALE NewsBot/1.0)");
        conn.setInstanceFollowRedirects(true);

        try (InputStream is = conn.getInputStream()) {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(is);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("item");
            for (int i = 0; i < Math.min(nodeList.getLength(), 10); i++) {
                Element el = (Element) nodeList.item(i);
                RssNewsItem item = new RssNewsItem();
                item.setTitle(getTagText(el, "title"));
                item.setDescription(stripHtml(getTagText(el, "description")));
                item.setLink(getTagText(el, "link"));
                item.setSource(sourceName);
                item.setPubDate(parseDate(getTagText(el, "pubDate")));
                item.setImageUrl(extractImageUrl(el));
                if (item.getTitle() != null && !item.getTitle().isBlank()) {
                    items.add(item);
                }
            }
        } finally {
            conn.disconnect();
        }
        return items;
    }

    private String getTagText(Element el, String tagName) {
        NodeList nl = el.getElementsByTagName(tagName);
        if (nl.getLength() > 0 && nl.item(0).getFirstChild() != null) {
            return nl.item(0).getFirstChild().getNodeValue();
        }
        return null;
    }

    private String extractImageUrl(Element el) {
        NodeList enclosure = el.getElementsByTagName("enclosure");
        if (enclosure.getLength() > 0) {
            Element enc = (Element) enclosure.item(0);
            String type = enc.getAttribute("type");
            if (type != null && type.startsWith("image")) {
                return enc.getAttribute("url");
            }
        }
        NodeList media = el.getElementsByTagName("media:content");
        if (media.getLength() > 0) {
            return ((Element) media.item(0)).getAttribute("url");
        }
        NodeList thumb = el.getElementsByTagName("media:thumbnail");
        if (thumb.getLength() > 0) {
            return ((Element) thumb.item(0)).getAttribute("url");
        }
        return null;
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null) return null;
        String[] formats = {
            "EEE, dd MMM yyyy HH:mm:ss Z",
            "EEE, dd MMM yyyy HH:mm:ss zzz",
            "yyyy-MM-dd'T'HH:mm:ssXXX"
        };
        for (String fmt : formats) {
            try {
                return new SimpleDateFormat(fmt, Locale.ENGLISH).parse(dateStr);
            } catch (ParseException ignored) {}
        }
        return new Date();
    }

    private String stripHtml(String html) {
        if (html == null) return null;
        String stripped = html.replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ").trim();
        return stripped.length() > 200 ? stripped.substring(0, 200) + "..." : stripped;
    }

    public static class RssNewsItem {
        private String title;
        private String description;
        private String link;
        private String source;
        private Date pubDate;
        private String imageUrl;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public Date getPubDate() { return pubDate; }
        public void setPubDate(Date pubDate) { this.pubDate = pubDate; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}
