package cc.heroy.douban.bean;
/**
 * title :书名
 * race :评分
 * racepeople :评价人数
 * info ：详细信息
 * type :类型 
 * story :剧情
 * url :详情页链接
 * 
 */
public class Book {
	private String title ;
	private String race ;
	private String racepeople ;
	private String info ;
	//private String type ;
	//private String story ;
	private String url ;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRace() {
		return race;
	}
	public void setRace(String race) {
		this.race = race;
	}
	public String getRacepeople() {
		return racepeople;
	}
	public void setRacepeople(String racepeople) {
		this.racepeople = racepeople;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString() {
		
		return "Movie [title=" + title + ", race=" + race + ", racepeople=" + racepeople + ", info=" + info + ", url=" + url
				+ "]";
	
	}

}
