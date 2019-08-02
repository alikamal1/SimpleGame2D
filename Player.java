import java.awt.*;

public class Player{
	
	//field 
	private int x;
	private int y;
	private int r;
	
	private int dx;
	private int dy;
	private int speed;
	
	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
	
	private int mx;
	private int my;
	
	private boolean firing;
	private long firingTimer;
	private long firingDelay;
	
	private boolean recovering;
	private long recoveryTimer;
	
	private int lives;
	private Color color1;
	private Color color2;
	
	private int score;
	
	private int powerLevel;
	private int power;
	private int[] requiredPower={2,4,6,8,8};
	private long time;
	
	private boolean shield;
		
	//constructor
	public Player(){
		
		x=GamePanel.WIDTH/2;
		y=GamePanel.HEIGHT/2+40;
		r=14;
		
		dx=0;
		dy=0;
		mx=GamePanel.WIDTH/2;
		my=GamePanel.HEIGHT/2+40;
		speed=5;
		lives=3;
		color1=Color.WHITE;
		color2=Color.RED;
		
		firing=false;
		firingTimer=System.nanoTime();
		firingDelay=200;
		
		recovering=false;
		recoveryTimer=0;
		score=0;
		
		powerLevel=0;
		
		shield=false;

	}
	
	//fuction
	
	
	public int getx(){return x;}
	public int gety(){return y;}
	public int getr(){return r;}
	
	public int getScore(){return score;}
	public int getLives(){return lives;}
	
	public boolean isRecovering(){return recovering;}
	public boolean isDead(){return lives<=0;}
	public boolean isShield(){return shield;}
	
	public void setLeft(boolean b){left=b;}
	public void setRight(boolean b){right=b;}
	public void setUp(boolean b){up=b;}
	public void setDown(boolean b){down=b;}
	
	public void setFiring(boolean b){firing=b;}
	
	public void addScore(int i){score+=i;}
	public void gainLife(){lives++;}
	
	public void shieldSet(boolean s){shield=s;}
	public boolean getShield(){return shield;}
	
	public long getTime(){return time;}
	
	public void playerReset(){
		x=GamePanel.WIDTH/2;
		y=GamePanel.HEIGHT/2+40;
		dx=0;
		dy=0;
		lives=3;
		score=0;
		powerLevel=0;
	
	}

	public void increasePower(int i){
		power+=i;
		if(powerLevel<4)
		{
			if(power>=requiredPower[powerLevel]){
				if(powerLevel<3)power-=requiredPower[powerLevel];
				powerLevel++;
			}
			if(powerLevel>3)power=requiredPower[powerLevel];
		}
		else {
			power=requiredPower[powerLevel];
			
		}
	}
	
	public int getPowerLevel(){return powerLevel;}
	public int getPower(){return power;}
	public int getRequiredPower(){return requiredPower[powerLevel];	}
	
	public void loseLife(){
		lives--;
		if(powerLevel>0){
		powerLevel--;
		power=0;
		}
		recovering=true;
		recoveryTimer=System.nanoTime();
		time=System.nanoTime();
	}
	
	public void setPlayer(int mpx,int mpy){
		mx=mpx;
		my=mpy;
		
	}
	
	public void update(){
	
	
		//keyboard
		if(left||right||up||down)
		{
		if(left){
			dx=-speed;
			x+=dx;
			}
			
		if(right){
			dx=speed;
			x+=dx;
			}
			
		if(up){
			dy=-speed;
			y+=dy;
		}
		
		if(down){
			dy=speed;
			y+=dy;
		}
		mx=x;
		my=y;
		}
		else{
		//mouse
		if(mx>x){
			
			x=mx;
		}
		
		if(mx<x){
			x=mx;
		}
		
		if(my>y){
			y=my;
		}
		
		if(my<y){
			y=my;
		}
		}
		if(x<r+16){x=r+16;}
		if(y<r+16){y=r+16;}
		if(x>GamePanel.WIDTH-(r+16)){x=GamePanel.WIDTH-(r+16);}
		if(y>GamePanel.HEIGHT-(r+16)){y=GamePanel.HEIGHT-(r+16);}
		
		dx=0;
		dy=0;
	
		//firing
		if(firing){
			long elapsed=(System.nanoTime()-firingTimer)/1000000;
			if(elapsed>firingDelay){
				firingTimer=System.nanoTime();
				if(powerLevel==0){
					GamePanel.bullets.add(new Bullet(270,x,y));
				}
				else if(powerLevel==1){
					GamePanel.bullets.add(new Bullet(270,x+5,y));
					GamePanel.bullets.add(new Bullet(270,x-5,y));
				}
				else if(powerLevel==2){
					GamePanel.bullets.add(new Bullet(270,x,y));
					GamePanel.bullets.add(new Bullet(275,x+5,y));
					GamePanel.bullets.add(new Bullet(265,x-5,y));
				}	
				else if(powerLevel==3){
					GamePanel.bullets.add(new Bullet(272,x+1,y));
					GamePanel.bullets.add(new Bullet(268,x-1,y));
					GamePanel.bullets.add(new Bullet(276,x+2,y));
					GamePanel.bullets.add(new Bullet(265,x-2,y));
				}
				else if(powerLevel==4){
					GamePanel.bullets.add(new Bullet(270,x,y));
					GamePanel.bullets.add(new Bullet(274,x+1,y));
					GamePanel.bullets.add(new Bullet(266,x-1,y));
					GamePanel.bullets.add(new Bullet(277,x+2,y));
					GamePanel.bullets.add(new Bullet(263,x-2,y));
				}
		
			}
		}
		
		if(recovering){
			long elapsed=(System.nanoTime()-recoveryTimer)/1000000;
			if(elapsed>2000){
				recovering=false;
				recoveryTimer=0;
			}
		}
		
	
	}
	
	public void draw(Graphics2D g){
		if(recovering){
			g.setColor(color2);
			g.fillOval(x-r,y-r,2*r,2*r);
			g.setStroke(new BasicStroke(3));
			g.setColor(color2.darker());
			g.drawOval(x-r,y-r,2*r,2*r);
			g.setStroke(new BasicStroke(1));
			g.drawOval(x-2*r,y-2*r,4*r,4*r);
		}
		else if(shield){
			g.setColor(Color.GREEN);
			g.fillOval(x-r,y-r,2*r,2*r);
			g.setStroke(new BasicStroke(3));
			g.setColor(Color.GREEN.darker());
			g.drawOval(x-r,y-r,2*r,2*r);
			g.setStroke(new BasicStroke(1));
			g.drawOval(x-2*r,y-2*r,4*r,4*r);
		}
		else{
			g.setColor(color1);
			g.fillOval(x-r,y-r,2*r,2*r);
			g.setStroke(new BasicStroke(3));
			g.setColor(color1.darker());
			g.drawOval(x-r,y-r,2*r,2*r);
			g.setStroke(new BasicStroke(1));
			g.drawOval(x-2*r,y-2*r,4*r,4*r);
		}
	}
}	
	