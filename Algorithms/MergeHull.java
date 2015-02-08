/* Merges two convex hulls and finds the combined convex hull
 */
import java.util.Arrays;
import java.util.Comparator;

public class MergeHull {
	
	public static void sortX(Point[] points) {
		
		Comparator<Point> comp = new Comparator<Point>() {
			public int compare(Point point1, Point point2) {
				if(point1.getX() < point2.getX())
					return -1;
				else if(point1.getX() > point2.getX())
					return 1;
				else
					return 0;
			}
		};
		
		Arrays.sort(points, comp); // O(nlgn) stable Mergesort
	}
	
	public static Point[] mergeHulls(Point[] leftHull, Point[] rightHull) {
		int i;
		
		// Finding right-most point on leftHull
		int rightmostInLeft = 0;
		for(i=1; i < leftHull.length; i++) {
			if(leftHull[i].getX() > leftHull[rightmostInLeft].getX())
				rightmostInLeft = i;
		}
		// Finding left-most point on rightHull
		int leftmostInRight = 0;
		for(i=1; i < rightHull.length; i++) {
			if(rightHull[i].getX() < rightHull[leftmostInRight].getX())
				leftmostInRight = i;
		}
		
		// Finding the lower bridge
		int lowerleft = rightmostInLeft, lowerright = leftmostInRight;
		int leftnext = rightmostInLeft-1, rightnext = (leftmostInRight+1)%rightHull.length;
		if(leftnext < 0)  leftnext = leftHull.length - 1;
		boolean leftok = false, rightok = false;
		while(!leftok || !rightok) {
			leftok = false; rightok = false;
			int leftval = rightHull[lowerright].orient(leftHull[lowerleft], leftHull[leftnext]);
			int rightval = leftHull[lowerleft].orient(rightHull[lowerright], rightHull[rightnext]);
			if(leftval >= 0) {   // CW motion
				if(leftval == 0 && rightHull[lowerright].distance(leftHull[lowerleft]) > rightHull[lowerright].distance(leftHull[leftnext]))
					leftok = false;
				else
					leftok = true;
			}
			if(rightval <= 0) {   // CCW motion
				if(rightval == 0 && leftHull[lowerleft].distance(rightHull[lowerright]) > leftHull[lowerleft].distance(rightHull[rightnext]))
					rightok = false;
				else
					rightok = true;
			}
			if(!leftok) {
				lowerleft = leftnext;
				leftnext = ((lowerleft-1)<0)?leftHull.length-1:(lowerleft-1);
			}
			if(!rightok) {
				lowerright = rightnext;
				rightnext = (lowerright+1) % rightHull.length;
			}
		}
				
		// Finding the upper bridge
		int upperleft = rightmostInLeft, upperright = leftmostInRight;
		leftnext = (rightmostInLeft+1)%leftHull.length; rightnext = leftmostInRight-1;
		if(rightnext < 0)  rightnext = rightHull.length - 1;
		leftok = false; rightok = false;
		while(!leftok || !rightok) {
			leftok = rightHull[upperright].orient(leftHull[upperleft], leftHull[leftnext]) <= 0;  // CCW motion
			rightok = leftHull[upperleft].orient(rightHull[upperright], rightHull[rightnext]) >= 0; // CW motion
			if(!leftok) {
				upperleft = leftnext;
				leftnext = (upperleft+1)%leftHull.length;
			}
			if(!rightok) {
				upperright = rightnext;
				rightnext = ((upperright-1)<0)?rightHull.length-1:(upperright-1);
			}
		}
		
		// Merging Hulls
		Point[] mergedHull = new Point[leftHull.length + rightHull.length];
		int mergedIndex = 0;
		if(leftHull[lowerleft].getY() <= rightHull[lowerright].getY()) {
			for(i=0; i<=lowerleft; i++)
				mergedHull[mergedIndex++] = leftHull[i];
			for(i=lowerright; i<=upperright; i++)
				mergedHull[mergedIndex++] = rightHull[i];
			if(upperleft != 0)
				for(i=upperleft; i<leftHull.length; i++)
					mergedHull[mergedIndex++] = leftHull[i];
		}
		else {
			for(i=0; i<=upperright; i++)
				mergedHull[mergedIndex++] = rightHull[i];
			if(lowerleft > upperleft)
				for(i=upperleft; i<=lowerleft; i++)
					mergedHull[mergedIndex++] = leftHull[i];
			else if(lowerleft == upperleft)
				mergedHull[mergedIndex++] = leftHull[lowerleft];
			else {
				for(i=upperleft; i<leftHull.length; i++)
					mergedHull[mergedIndex++] = leftHull[i];
				for(i=0; i<=lowerleft; i++)
					mergedHull[mergedIndex++] = leftHull[i];
			}
			if(lowerright != 0)
				for(i=lowerright; i<rightHull.length; i++)
					mergedHull[mergedIndex++] = rightHull[i];
		}
		
		// Need an accurate length property of the resulting array		
		return (Arrays.copyOf(mergedHull, mergedIndex));
	}
	
	/* Assumes input argument points[] to be sorted already */
	public static Point[] findHull(final Point[] points) {
		if(points.length == 1)
			return points;
		
		Point[] pointsCopy = Arrays.copyOf(points, points.length);		
		Point[] leftHalf = Arrays.copyOf(pointsCopy, pointsCopy.length/2);
		Point[] rightHalf = Arrays.copyOfRange(pointsCopy, pointsCopy.length/2, pointsCopy.length);
		
		leftHalf = MergeHull.findHull(leftHalf);
		rightHalf = MergeHull.findHull(rightHalf);
		
		return (MergeHull.mergeHulls(leftHalf, rightHalf));
	}

	public static void main(String[] args) {
		Point[] points = {
			new Point('A', 0, 0),
		    new Point('B', -5, -2),
		    new Point('C', -2, -1),
		    new Point('D', -6, 0),
		    new Point('E', -3.5, 1),
		    new Point('F', -4.5, 1.5),
		    new Point('G', -2.5, -5),
		    new Point('H', 1, -2.5),
		    new Point('I', 2.5, 0.5),
		    new Point('J', -2.2, 2.2)
		};
		
		MergeHull.sortX(points);
		points = MergeHull.findHull(points);
		System.out.println("The Convex Hull is:-");
		for(int i=0; i<points.length; i++)
			System.out.println(points[i].toString());
	}

}
