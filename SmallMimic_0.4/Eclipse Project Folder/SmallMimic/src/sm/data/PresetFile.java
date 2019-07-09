package sm.data;
import java.awt.Point;
import java.io.Serializable;
import java.util.LinkedHashMap;

public class PresetFile implements Serializable {
	private LinkedHashMap<String, Point> targets = new LinkedHashMap<>();
	
	public LinkedHashMap<String, Point> getTargets() {
		return this.targets;
	}
	
	public void addTarget(String s, Point p) {
		targets.put(s, p);
	}
	
	public void removeTarget(String s) {
		targets.remove(s);
	}
}