import java.awt.*;

class BossBullet{
	
	//field
	private double x;
	private double y;
	private int r;
	
	private double dx;
	private double dy;
	private double rad;
	private double speed;
	private double angle;
	
	private Color color1;
	
	
	//constructor
	public BossBullet(double x,double y){
		this.x=x;
		this.y=y;
		r=6;
		
		angle=Math.random()*350+200;
		rad=Math.toRadians(angle);
		dx=Math.cos(rad);
		dy=Math.sin(rad);
		
		color1=Color.RED;
	
	}
	public boolean hitPlayer(double px,double py,double pr){
		double bx=x;
		double by=y;
		double br=r;
		
		double diffx=px-bx;
		double diffy=py-by;
		double dist=Math.sqrt(diffx*diffx+diffy*diffy);
		if(dist<pr+br){
			return true;
		}
		return false;
	}
	
	public boolean update(){
		
		
		x+=dx*2;
		y+=dy*2;

		if(false){
			return true;
		}
		else {
			return false;
		}
	}
		public void draw(Graphics2D g){
			g.setColor(color1);
			g.fillOval((int)(x-r),(int)(y-r),2*r,2*r);
		}
	
		
	
}