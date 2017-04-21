package webservice;

public class EventLogResponse {

	private int p1;
	private int p2;
	private EventType eventType;
	private long date;
	
	public int getP1() {
		return p1;
	}
	
	public void setP1(int p1) {
		this.p1 = p1;
	}
	
	public int getP2() {
		return p2;
	}
	
	public void setP2(int p2) {
		this.p2 = p2;
	}
	
	public EventType getEventType() {
		return eventType;
	}
	
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		this.date = date;
	}
}
