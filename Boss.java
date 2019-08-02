import java.awt.*;

public class Boss{
	
//filed
private double x;
private double y;
private int r;

private double dx;
private double dy;
private double speed;

private int health;
private boolean dead;
private boolean hit;
private long hitTimer;

private Color color1=new Color(255,140,0);

private long start;
private long startTime=6000;

private long bTime;
private long bTimeDiff=100;

private long time;

//constructor
public Boss(){
	x=400;
	y=240;
	r=50;
	dx=0.5;
	dy=0;
	color1=Color.RED;
	
	health=340;
	dead=false;
	hit=false;
	hitTimer=0;
	start=System.nanoTime();
	bTime=System.nanoTime();

}

//function

public double getx(){return x;}
public double gety(){return y;}
public double getr(){return r;}
public int getHealth(){return health;}
public void deadSet(boolean b){dead=b;}


public boolean isDead(){return dead;}
public long getTime(){return time;}

public void hit(){
	health--;
	if(health==0)time=System.nanoTime();
	if(health<=0){
		dead=true;
	}
	hit=true;
	hitTimer=System.nanoTime();
}

public void update(){
	
	long elapsed2=(System.nanoTime()-bTime)/1000000;
	if(elapsed2>bTimeDiff){
		GamePanel.bossBullets.add(new BossBullet(x,y));
		bTime=System.nanoTime();
	}
	
	long elapsed1=(System.nanoTime()-start)/1000000;
	if(elapsed1>6000){
	x+=dx;
	y+=0;
	if(x-r<0&&dx<0){dx=-dx;}
	if(y-r<0&&dy<0){dy=-dy;}
	if(x+r>GamePanel.WIDTH&&dx>0){dx=-dx;}
	if(y+r>GamePanel.HEIGHT&&dy>0){dy=-dy;}
	}
	if(hit){
		long elapsed=(System.nanoTime()-hitTimer)/1000000;
		if(elapsed>50){
			hit=false;
			hitTimer=0;
		}
	}
}
public void draw(Graphics2D g){
	if(hit){
	g.setColor(Color.YELLOW);
	g.fillOval((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(10));
	g.setColor(Color.YELLOW.darker());
	g.drawOval((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(2));
	}
	else{
	g.setColor(color1);
	g.fillOval((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(10));
	g.setColor(color1.darker());
	g.drawOval((int)(x-r),(int)(y-r),2*r,2*r);
	g.setStroke(new BasicStroke(2));
	}

}
}