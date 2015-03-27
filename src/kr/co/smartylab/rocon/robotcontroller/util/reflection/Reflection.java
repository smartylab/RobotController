package kr.co.smartylab.rocon.robotcontroller.util.reflection;

public class Reflection {
	@SuppressWarnings("rawtypes")
	public static Class getClass(String type) {
		if(type.equals("int"))		return int.class;
		if(type.equals("long"))		return long.class;
		if(type.equals("float"))	return float.class;
		if(type.equals("double"))	return double.class;
		if(type.equals("String"))	return String.class;
		return null;
	}
}
