package com.stien.festivaljakt.slottsfjell;


public class Tag {
	private String _name;
	private long _time;


	public Tag(String name, long timestamp) {
		_name = name;
		_time = timestamp;
	}


	public String getName() {
		return _name;
	}

	public long getTime() {
		return _time;
	}

	public String getPrettyTime() {
		int d = (int)( _time / 86400 );
		int h = (int)( _time % 86400 ) / 3600;
		int m = (int)( _time % 3600 ) / 60;
		int s = (int)( _time % 60 );

		if (d != 0) {
			return  getPrettyElement("dag", "dager", d) + ", " +
					getPrettyElement("time", "timer", h);
		} else if (h != 0) {
			return  getPrettyElement("time", "timer", h) + ", " +
					getPrettyElement("minutt", "minutter", m);
		} else if (m != 0) {
			return getPrettyElement("minutt", "minutter", m);
		} else {
			return "Akkurat nå";
		}
	}

	public static String getPrettyElement(String singular, String plural, int count) {
		if (count == 1) {
			return count + " " + singular;
		} else {
			return count + " " + plural;
		}
	}
}
