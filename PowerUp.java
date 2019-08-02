import java.awt.*;

public class PowerUp{
	
	//field 
	private double x;
	private double y;
	private int r;
		
	private int type;
	private long start;
	private long time=3000;
	
	//1 -- +1 life
	//2 -- +1 power
	//3 -- +2 power
	//4 -- slow down time
	//5 -- clear enemy
	//6 -- shield
	
	//constructor
	public PowerUp(int type,double x,double y,long s){
		this.type=type;
		this.x=x;
		this.y=y;
		this.start=s;
	
	if(type==1){
		r=6;
	}
	if(type==2){
		r=6;
	}
	if(type==3){
		r=6;
	}
	if(type==4){
		r=6;
	}
	if(type==5){
		r=6;
	}
	if(type==6){
		r=6;
	}
	
	}
	//function
	public double getx(){return x;}
	public double gety(){return y;}
	public double getr(){return r;}
	public int getType(){return type;}
	
	public boolean update(){
		y+=2;
		if(y>GamePanel.HEIGHT+r){
			return true;
		}
		return false;
	}

	public void draw(Graphics2D g){
	long elapsed=(System.nanoTime()-start)/1000000;
	int alpha=(int)(255*Math.sin(3.14*elapsed/time));
	if(alpha>255)alpha=0;
	if(alpha<0)alpha=255;	
	if(type==1){
	g.setColor(new Color(246,41,176,alpha));
	g.fillRect((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(2));
	g.setColor(new Color(181,23,155,alpha));
	g.drawRect((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(1));
	}
	if(type==2){
	g.setColor(new Color(255,247,0,alpha));
	g.fillRect((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(2));
	g.setColor(new Color(224,188,7,alpha));
	g.drawRect((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(1));
	}
	if(type==3){
	g.setColor(new Color(255,247,0,alpha));
	g.fillRect((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(2));
	g.setColor(new Color(224,188,7,alpha));
	g.drawRect((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(1));
	}
	if(type==4){
	g.setColor(new Color(201,201,201,alpha));
	g.fillRect((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(2));
	g.setColor(new Color(148,148,148,alpha));
	g.drawRect((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(1));
	}
	if(type==5){
	g.setColor(new Color(255,140,0,alpha));
	g.fillRect((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(2));
	g.setColor(new Color(207,89,11,alpha));
	g.drawRect((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(1));
	}
	if(type==6){
	g.setColor(new Color(0,255,43,alpha));
	g.fillRect((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(2));
	g.setColor(new Color(0,173,29,alpha));
	g.drawRect((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(1));
	}	
}
}
		