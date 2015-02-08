public class Point {
	
    private char label;
    private double x;
    private double y;

    public Point() {
    	label = ' ';
    	x = y = 0;
    }
    
    public Point(char label, double x, double y) {
    	this.label = label;
    	this.x = x;
    	this.y = y;
    }

    public double getX() {
    	return x;
    }
    public double getY() {
    	return y;
    }
    

    public int orient(Point b, Point c) {
		double lhs = (b.y - this.y) * (c.x - this.x);
		double rhs = (c.y - this.y) * (b.x - this.x);
		if (lhs > rhs)
			return 1;
		else if (lhs < rhs)
			return -1;
		else
			return 0;
    }
    
    public double distance(Point a) {
    	return (Math.sqrt(Math.pow(a.getY() - this.getY(), 2) + Math.pow(a.getX() - this.getX(), 2)));
    }
    public static double distance(Point a, Point b) {
    	return (Math.sqrt(Math.pow(b.getY() - a.getY(), 2) + Math.pow(b.getX() - a.getX(), 2)));
    }
    
    
    @Override
    public String toString() {
	return label+"[x=" + x + ", y=" + y + "]";
    }
}
