package cis3334.java_webaip;


import java.util.Date;
import java.util.List;

// get a list of space news articles from
// https://spaceflightnewsapi.net/
// https://test.spaceflightnewsapi.net/api/v2/articles
// Java from https://json2csharp.com/json-to-pojo
public class SpaceNews {
    public int id;
    public String title;
    public String url;
    public String image_url;
    public String news_site;
    public String summary;
    public String published_at;
    public String updated_at;
    public boolean featured;
    // launches/events arrays exist but we skip them here
}