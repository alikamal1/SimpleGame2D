import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
public class GamePanel extends JPanel implements Runnable,KeyListener,MouseListener,MouseMotionListener{
	
	//field 
	public static int WIDTH=800;
	public static int HEIGHT=645;
	
	private Thread thread;
	private boolean running;
	
	private boolean pause=false;
	
	private BufferedImage image;
	private Graphics2D g;
	
	private BufferedImage image2;
	private Graphics2D g2;
	
	private int FPS=30;
	private double averageFPS;
	
	public static Player player;
	public static Boss boss;
	public static ArrayList<Bullet> bullets;
	public static ArrayList<Enemy> enemies;
	public static ArrayList<PowerUp> powerups;
	public static ArrayList<Explosion> explosions;
	public static ArrayList<Text> texts;
	public static ArrayList<BossBullet> bossBullets;
	
	private long waveStartTimer;
	private long waveStartTimerDiff;
	private int waveNumber=0;
	private boolean waveStart;
	private int waveDelay=2000;
	
	private long slowDownTimer;
	private long slowDownTimerDiff;
	private int slowDownLength=6000;
	
	private long explosionTimer;
	private long explosionTimerDiff;
	private int explosionLength=2000;
	
	private boolean clear=false;
	private boolean isBoss=false;
	private boolean restart=false;
	private boolean win=false;
	private boolean start=false;
	private boolean gameOver=false;
	
	private long shieldLength=5000;
	private long shieldTimer;
	private long shieldDiff;
	
	private long time=2000;
	private long startb;

	private boolean cheat=false;
	//constructor
	public GamePanel(){
		super();
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		setFocusable(true);
		requestFocus();
	}
	
	//function
	public void addNotify(){
		super.addNotify();
		if(thread==null){
			thread=new Thread(this);
			thread.start();
		}
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		
	}
		
	//start menu	
	private void setStart(boolean s){start=s;}
	
	private boolean startMenu(){
		setStart(start);
		if(start){running=true;return true;}
			else return false;
	}
	
	public void run(){
	
		startb=System.nanoTime();
		while(!startMenu()){
			//draw menu
			image2=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
			g2=(Graphics2D)image2.getGraphics();	
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setColor(new Color(0,75,229) );
			g2.fillRect(0,0,WIDTH,HEIGHT);
			g2.setColor(Color.WHITE);
			g2.setFont(new Font("Hobo Std",Font.PLAIN,75));
			String s=" S p a c e   G u n ";
			int length=(int)g2.getFontMetrics().getStringBounds(s,g2).getWidth();
			g2.drawString(s,(WIDTH-length)/2,(HEIGHT/2)-140);
			s="2.0";
			g2.setColor(Color.GREEN);
			g2.setFont(new Font("Century Gothic",Font.PLAIN,30));
			g2.drawString(s,670,(HEIGHT/2)-140);	
			
			long elapsed=(System.nanoTime()-startb)/1000000;
			int alpha=(int)(255*Math.sin(3.14*elapsed/time));
			if(alpha>255)alpha=0;
			if(alpha<0)alpha=255;
			g2.setColor(new Color(255,0,24,alpha));
			g2.setFont(new Font("Showcard Gothic",Font.PLAIN,40));
			s="Press Enter To PLAY ";
			length=(int)g2.getFontMetrics().getStringBounds(s,g2).getWidth();
			g2.drawString(s,(WIDTH-length)/2,HEIGHT/2+50);
			g2.setStroke(new BasicStroke(1));
			
			s="- Programmered By -";
			g2.setFont(new Font("Hobo Std",Font.PLAIN,35));
			length=(int)g2.getFontMetrics().getStringBounds(s,g2).getWidth();
			g2.setColor(new Color(0,0,0));
			g2.drawString(s,(WIDTH-length)/2,HEIGHT/2+260);
			g2.setColor(new Color(255,255,255));
			s="A L I   K A M A L";
			g2.setFont(new Font("Hobo Std",Font.PLAIN,30));
			length=(int)g2.getFontMetrics().getStringBounds(s,g2).getWidth();
			g2.drawString(s,(WIDTH-length)/2,HEIGHT/2+300);
			
			Graphics g2=this.getGraphics();
			g2.drawImage(image2,0,0,null);
			g2.dispose();
		}
		
		//the Game
		image=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		g=(Graphics2D)image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		//invisible cursor
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Point hotSpot = new Point(0,0);
		BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT); 
		Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, hotSpot, "InvisibleCursor");        
		setCursor(invisibleCursor);
		
		player=new Player();
		boss=new Boss();
		bullets=new ArrayList<Bullet>();
		enemies=new ArrayList<Enemy>();
		powerups=new ArrayList<PowerUp>();
		explosions=new ArrayList<Explosion>();
		texts=new ArrayList<Text>();
		bossBullets=new ArrayList<BossBullet>();
		
		waveStartTimer=0;
		waveStartTimerDiff=0;
		waveStart=true;
		if(waveNumber==30)waveNumber=0;
		
		long startTime;
		long URBTimeMillis;
		long waitTime;
		long totalTime=0;
		
		int frameCount=0;
		int maxFrameCount=30;
		long targetTime=1000/FPS;

		//Game Loop
		while(running&&!win){
		
		//pause
		if(pause){
			g.setFont(new Font("Century Gothic",Font.PLAIN,44));
			String s="- P A U S E -";
			g.setColor(new Color(255,255,255));
		} 
		else{
			startTime=System.nanoTime();
			gameUpdate();
			gameRender();
			gameDraw();
			URBTimeMillis=(System.nanoTime()-startTime)/1000000;
			waitTime=targetTime-URBTimeMillis;
			
			try{
				thread.sleep(waitTime);
			}catch(Exception e){}
			
			totalTime+=System.nanoTime()-startTime;
			frameCount++;
			if(frameCount==maxFrameCount){
				averageFPS=1000.0/((totalTime/frameCount)/1000000);
				frameCount=0;
				totalTime=0;
			}
	
		while(gameOver){
		g.setColor(new Color(0,0,0));
		g.fillRect(0,0,WIDTH,HEIGHT);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Hobo Std",Font.PLAIN,65));
		String s="Game Over";
		int length=(int)g.getFontMetrics().getStringBounds(s,g).getWidth();
		g.drawString(s,(WIDTH-length)/2,(HEIGHT/2)-150);
		
		g.setColor(new Color(251,11,66));
		s="Final Score: "+player.getScore();
		length=(int)g.getFontMetrics().getStringBounds(s,g).getWidth();
		g.drawString(s,(WIDTH-length)/2,(HEIGHT/2)-10);
		
		long elapsed=(System.nanoTime()-startb)/1000000;
		int alpha=(int)(255*Math.sin(3.14*elapsed/time));
		if(alpha>255)alpha=0;
		if(alpha<0)alpha=255;
		s="-Prees R To Play Again!-";
		g.setColor(new Color(145,70,188,alpha));
		g.setFont(new Font("Hobo Std",Font.PLAIN,45));
		length=(int)g.getFontMetrics().getStringBounds(s,g).getWidth();
		g.drawString(s,(WIDTH-length)/2,HEIGHT/2+140);
		
		s="- Programmered By -";
		g.setFont(new Font("Hobo Std",Font.PLAIN,35));
		length=(int)g.getFontMetrics().getStringBounds(s,g).getWidth();
		g.setColor(new Color(210,105,30));
		g.drawString(s,(WIDTH-length)/2,HEIGHT/2+260);
		g.setColor(new Color(0,191,255));
		
		g.setColor(new Color(0,191,255));
		s="A L I   K A M A L";
		g.setFont(new Font("Hobo Std",Font.PLAIN,30));
		length=(int)g.getFontMetrics().getStringBounds(s,g).getWidth();
		g.drawString(s,(WIDTH-length)/2,HEIGHT/2+300);
		gameDraw();
		//game restart

		if(restart){
			restart=false;
			isBoss=false;
			gameOver=false;
			start=false;
			waveNumber=0;
			g.dispose();
			run();
			}
		}
		}
		
		
		//draw win window
		while(win){
		g.setColor(new Color(0,0,0));
		g.fillRect(0,0,WIDTH,HEIGHT);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Hobo Std",Font.PLAIN,55));
		String s="Congratulations You WON!!";
		int length=(int)g.getFontMetrics().getStringBounds(s,g).getWidth();
		g.drawString(s,(WIDTH-length)/2,(HEIGHT/2)-150);
		
		g.setColor(new Color(251,11,66));
		s="Final Score: "+player.getScore();
		length=(int)g.getFontMetrics().getStringBounds(s,g).getWidth();
		g.drawString(s,(WIDTH-length)/2,(HEIGHT/2)-10);
				
		long elapsed=(System.nanoTime()-startb)/1000000;
		int alpha=(int)(255*Math.sin(3.14*elapsed/time));
		if(alpha>255)alpha=0;
		if(alpha<0)alpha=255;
		s="-Prees R To Play Again!-";
		g.setColor(new Color(145,70,188,alpha));
		g.setFont(new Font("Hobo Std",Font.PLAIN,45));
		length=(int)g.getFontMetrics().getStringBounds(s,g).getWidth();
		g.drawString(s,(WIDTH-length)/2,HEIGHT/2+140);
		
		s="- Programmered By -";
		g.setFont(new Font("Hobo Std",Font.PLAIN,35));
		length=(int)g.getFontMetrics().getStringBounds(s,g).getWidth();
		g.setColor(new Color(210,105,30));
		g.drawString(s,(WIDTH-length)/2,HEIGHT/2+260);
		g.setColor(new Color(0,191,255));
		
		g.setColor(new Color(0,191,255));
		s="A L I   K A M A L";
		g.setFont(new Font("Hobo Std",Font.PLAIN,30));
		length=(int)g.getFontMetrics().getStringBounds(s,g).getWidth();
		g.drawString(s,(WIDTH-length)/2,HEIGHT/2+300);
		gameDraw();
		//game restart
		if(restart){
			win=false;
			start=false;
			restart=false;
			isBoss=false;
			g.dispose();
			run();

		}
		}
	}
	}
	private void gameUpdate(){
	

	//new wave
	if(waveStartTimer==0&&enemies.size()==0&&clear==false&&isBoss==false){
		waveNumber++;
		waveStart=false;
		waveStartTimer=System.nanoTime();
	}
	else{
		waveStartTimerDiff=(System.nanoTime()-waveStartTimer)/1000000;
		if(waveStartTimerDiff>waveDelay){
			waveStart=true;
			waveStartTimer=0;
			waveStartTimerDiff=0;
		}
	}
	
	//create enemy
	if(waveStart&&enemies.size()==0&&clear==false){
		createNewEnemies();
	}
	
	//player update
	player.update();
	
	//bullet update
	for(int i=0;i<bullets.size();i++){
		boolean remove=bullets.get(i).update();
		if(remove){
			bullets.remove(i);
			i--;
		}
	}
	
	//enemy update
	for(int i=0;i<enemies.size();i++){
		enemies.get(i).update();
	}
	
	//boss update
	if(isBoss){
		boss.update();	
		
		//boss bullet update
		for(int i=0;i<bossBullets.size();i++){
			boolean remove=bossBullets.get(i).update();
			if(remove){
				bossBullets.remove(i);
				i--;
			}
		}
	}
	
	//powerup update
	for(int i=0;i<powerups.size();i++){
		boolean remove=powerups.get(i).update();
		if(remove){
			powerups.remove(i);
			i--;
		}
	}
	
	//text update
	for(int i=0;i<texts.size();i++){
		boolean remove=texts.get(i).update();
		if(remove){
			texts.remove(i);
			i--;
		}
	}
	
	//explosion update
	for(int i=0;i<explosions.size();i++){
		boolean remove=explosions.get(i).update();
		if(remove){
			explosions.remove(i);
			i--;
		}
	}
	
	//bullet-enemy collison
	for(int i=0;i<bullets.size();i++){
		Bullet b=bullets.get(i);
		
		double bx=b.getx();
		double by=b.gety();
		double br=b.getr();
		
		for(int j=0;j<enemies.size();j++){
			Enemy e=enemies.get(j);
			
			double ex=e.getx();
			double ey=e.gety();
			double er=e.getr();
			
			double dx=bx-ex;
			double dy=by-ey;
			double dist=Math.sqrt(dx*dx+dy*dy);
			
			if(dist<br+er){
				e.hit();
				bullets.remove(i);
				i--;
				break;
			}
		}
	}
	
	if(isBoss){
		//bullet-enemy collison
		for(int i=0;i<bullets.size();i++){
			Bullet b=bullets.get(i);
			
			double bx=b.getx();
			double by=b.gety();
			double br=b.getr();
			
			double sx=boss.getx();
			double sy=boss.gety();
			double sr=boss.getr();
			
			double dx=bx-sx;
			double dy=by-sy;
			double dist=Math.sqrt(dx*dx+dy*dy);
			
			if(dist<br+sr){
				boss.hit();
				explosions.add(new Explosion((double)boss.getx(),(double)boss.gety(),(int)boss.getr(),(int)boss.getr()+50));
				bullets.remove(i);
				String s="+5";
				player.addScore(5);
				texts.add(new Text(boss.getx()-40+70*Math.random(),boss.gety()-60-30*Math.random(),500,s));
				i--;
				break;
			}
		}
		
		//player-boss bullet collison
		for(int i=0;i<bossBullets.size();i++){
			BossBullet b=bossBullets.get(i);
			if(b.hitPlayer(player.getx(),player.gety(),player.getr())&&!player.isRecovering()){
				bossBullets.remove(i);
				if(!cheat)player.loseLife();
			}
		}
	}
	
	//clear enemy
	if(clear){
		for(int i=0;i<enemies.size();i++){
			Enemy e=enemies.remove(i);
			explosions.add(new Explosion(e.getx(),e.gety(),e.getr(),e.getr()+20));
			player.addScore(e.getType()+e.getRank());
		}
	}
	//check dead enemy
	for(int i=0;i<enemies.size();i++){
		if(enemies.get(i).isDead()){
			Enemy e=enemies.get(i);
			
			//chance for powerup
			double rand=Math.random();
			if(powerups.size()<2){
			if(rand<0.006){if(player.getLives()<4)powerups.add(new PowerUp(1,e.getx(),e.gety(),System.nanoTime()));}
			else if(rand<0.030){if(player.getPowerLevel()<4)powerups.add(new PowerUp(2,e.getx(),e.gety(),System.nanoTime()));}
			else if(rand<0.040){if(player.getPowerLevel()<4)powerups.add(new PowerUp(3,e.getx(),e.gety(),System.nanoTime()));}
			else if(rand<0.045)powerups.add(new PowerUp(4,e.getx(),e.gety(),System.nanoTime()));
			else if(rand<0.050)powerups.add(new PowerUp(5,e.getx(),e.gety(),System.nanoTime()));
			else if(rand<0.055)powerups.add(new PowerUp(6,e.getx(),e.gety(),System.nanoTime()));
			}
			player.addScore(e.getType()+e.getRank());
			enemies.remove(i);
			i--;
			
			e.explode();
			explosions.add(new Explosion(e.getx(),e.gety(),e.getr(),e.getr()+20));
			String s="+"+(e.getType()+e.getRank());
			texts.add(new Text(e.getx(),e.gety(),500,s));
		}
	}
	
	//check dead player
	if(player.isDead()){
		for(int i=0;i<bullets.size();i++)
			bullets.remove(i);
		long elapsedb1=(System.nanoTime()-player.getTime())/1000000;
		explosions.add(new Explosion(player.getx(),player.gety(),(int)player.getr(),(int)player.getr()+100));		
		if(elapsedb1>2000)
		gameOver=true;
	}
	
	//check dead boss
	if(boss.isDead()){
		for(int i=0;i<bossBullets.size();i++)
			bossBullets.remove(i);
		for(int j=0;j<enemies.size();j++){
			Enemy e=enemies.get(j);
			explosions.add(new Explosion(e.getx(),e.gety(),e.getr(),e.getr()+20));
			enemies.remove(j);
		}
		long elapsedb=(System.nanoTime()-boss.getTime())/1000000;
		explosions.add(new Explosion(boss.getx(),boss.gety(),(int)boss.getr(),(int)boss.getr()+200));		
		if(elapsedb>4000){win=true;}
	}
	//checkk player enemy collision 
	if(!player.isRecovering()){
		int px=player.getx();
		int py=player.gety();
		int pr=player.getr();
		for(int i=0;i<enemies.size();i++){
			Enemy e=enemies.get(i);
			
			double ex=e.getx();
			double ey=e.gety();
			double er=e.getr();
		
			double dx=px-ex;
			double dy=py-ey;
			double dist=Math.sqrt(dx*dx+dy*dy);
		
			if(dist<pr+er&&!player.getShield()){
				if(!cheat)player.loseLife();
			}
		}
	}
	
	//player-powerup collision
	int px=player.getx();
	int py=player.gety();
	int pr=player.getr();
	for(int i=0;i<powerups.size();i++){
		PowerUp p=powerups.get(i);
		
		double x=p.getx();
		double y=p.gety();
		double r=p.getr();
		
		double dx=px-x;
		double dy=py-y;
		double dist=Math.sqrt(dx*dx+dy*dy);
		
		//collected powerup
		if(dist<pr+r){
			int type=p.getType();
			if(type==1){
				if(player.getLives()<4){player.gainLife();}
				texts.add(new Text(player.getx(),player.gety(),2000,"Extra Life"));
			}
			if(type==2){
				player.increasePower(1);
				texts.add(new Text(player.getx(),player.gety(),2000,"Power"));
			}
			if(type==3){
				player.increasePower(2);
				texts.add(new Text(player.getx(),player.gety(),2000,"Double Power"));
			}
			if(type==4){
				slowDownTimer=System.nanoTime();
				for(int j=0;j<enemies.size();j++){
					enemies.get(j).setSlow(true);
				}
				texts.add(new Text(player.getx(),player.gety(),2000,"Slow Down"));
			}
			if(type==5){
				clear=true;
				texts.add(new Text(player.getx(),player.gety(),2000,"Explosion"));
				explosionTimer=System.nanoTime();
			}
			if(type==6){
				texts.add(new Text(player.getx(),player.gety(),2000,"Shield"));
				player.shieldSet(true);
				shieldTimer=System.nanoTime();
			}
			powerups.remove(i);
			i--;
		}
	}
		
	//slow down update
	if(slowDownTimer!=0){
		slowDownTimerDiff=(System.nanoTime()-slowDownTimer)/1000000;
		if(slowDownTimerDiff>slowDownLength){
			slowDownTimer=0;
			for(int j=0;j<enemies.size();j++){
				enemies.get(j).setSlow(false);
			}
		}
	}
	
	//shield update
	if(player.getShield()){
		shieldDiff=(System.nanoTime()-shieldTimer)/1000000;
		if(shieldDiff>shieldLength){
			shieldTimer=0;
			player.shieldSet(false);
		}
	}
	//explosion clear update
	if(explosionTimer!=0)
	{
		explosionTimerDiff=(System.nanoTime()-explosionTimer)/1000000;
		if(explosionTimerDiff>explosionLength){
			explosionTimer=0;
			clear=false;
			}
		}
	}
	
	private void gameRender(){
		
		//draw background
		g.setColor(new Color(0,100,255));
		g.fillRect(0,0,WIDTH,HEIGHT);
		
		// slow down screen
		if(slowDownTimer!=0){
			g.setColor(new Color(255,255,255,64));
			g.fillRect(0,0,WIDTH,HEIGHT);
		}
		
		//draw clear explosions
		if(explosionTimer!=0||isBoss){
			g.setColor(new Color(255,140,0));
			g.fillRect(0,0,WIDTH,HEIGHT);
		}
		
		//draw player
		player.draw(g);
		
		//draw bullet
		for(int i=0;i<bullets.size();i++){
			bullets.get(i).draw(g);
		}
		
		//draw enemy
		for(int i=0;i<enemies.size();i++){
			enemies.get(i).draw(g);
		}
		
		//draw boss bullet
		if(isBoss){
			for(int i=0;i<bossBullets.size();i++){
				bossBullets.get(i).draw(g);
			}
		
			//draw boss
			boss.draw(g);
		}
		
		//draw powerup
		for(int i=0;i<powerups.size();i++){
			powerups.get(i).draw(g);
		}
		
		//draw text
		for(int i=0;i<texts.size();i++){
			texts.get(i).draw(g);
		}
		
		//draw explosions
		for(int i=0;i<explosions.size();i++){
			explosions.get(i).draw(g);
		}
		
		//draw wave nubmer
		if(waveStartTimer!=0){
			if(waveNumber<30){
			g.setFont(new Font("Century Gothic",Font.PLAIN,44));
			String s="- W A V E  "+waveNumber+" -";
			int length=(int)(g.getFontMetrics().getStringBounds(s,g).getWidth());
			int alpha=(int)(255*Math.sin(3.14*waveStartTimerDiff/waveDelay));
			if(alpha>255)alpha=255;
			g.setColor(new Color(255,255,255,alpha));
			g.drawString(s,WIDTH/2-length/2,HEIGHT/2);
			}
		else{
			g.setFont(new Font("Century Gothic",Font.PLAIN,44));
			String s="-  B O S S  -";
			int length=(int)(g.getFontMetrics().getStringBounds(s,g).getWidth());
			int alpha=(int)(255*Math.sin(3.14*waveStartTimerDiff/waveDelay));
			if(alpha>255)alpha=255;
			g.setColor(new Color(255,255,255,alpha));
			g.drawString(s,WIDTH/2-length/2,HEIGHT/2);
		}
		}
		//draw player lives
		for(int i=0;i<player.getLives();i++){
			g.setColor(Color.WHITE);
			g.setFont(new Font("Hobo Std",Font.PLAIN,20));
			g.drawString("- Lives -",70,30);
			g.setColor(Color.WHITE);
			g.fillOval(40+(40*i),40,player.getr()*2,player.getr()*2);
			g.setStroke(new BasicStroke(3));
			g.setColor(Color.WHITE.darker());
			g.drawOval(40+(40*i),40,player.getr()*2,player.getr()*2);
			g.setStroke(new BasicStroke(1));
		}
		
		//draw player power
		g.setColor(Color.YELLOW);
		g.setFont(new Font("Hobo Std",Font.PLAIN,20));
		g.drawString("- Power -",70,107);
		g.setColor(new Color(255,255,0));
		g.fillRect(40,113,player.getPower()*20,20);
		g.setColor(new Color(255,140,0));
		g.setStroke(new BasicStroke(2));
		for(int i=0;i<player.getRequiredPower();i++){
			g.drawRect(40+20*i,113,20,20);
		}
		g.setStroke(new BasicStroke(1));
		
		
		//draw score
		g.setColor(Color.WHITE);
		g.setFont(new Font("Hobo Std",Font.PLAIN,28));
		g.drawString("Score: "+player.getScore(),WIDTH-200,60);
		g.setColor(new Color(224,255,255));
		g.setStroke(new BasicStroke(2));
		g.drawRect(WIDTH-210,28,190,40);
		
		//draw slow down meter
		if(slowDownTimer!=0){
			g.setColor(Color.WHITE);
			g.setFont(new Font("Hobo Std",Font.PLAIN,16));
			g.drawString("- Slow Down -",100,157);
			g.setColor(Color.WHITE);
			g.drawRect(40,160,200,16);
			g.fillRect(40,160,200-((int)(100.0*slowDownTimerDiff/slowDownLength)*2),16);
		}
		
		//draw shield meter
		if(shieldTimer!=0){
			g.setColor(Color.GREEN);
			g.setFont(new Font("Hobo Std",Font.PLAIN,16));
			g.drawString("- Shield -",110,200);
			g.setColor(Color.GREEN);
			g.drawRect(40,205,200,16);
			g.fillRect(40,205,200-((int)(100.0*shieldDiff/shieldLength)*2),16);
		}
		
		//draw boss health
		if(isBoss){
			g.setColor(Color.YELLOW);
			g.setFont(new Font("Hobo Std",Font.PLAIN,28));
			g.drawString("B O S S",WIDTH/2-30,30);
			g.setColor(Color.RED);
			g.fillRect(242,41,boss.getHealth(),19);
			g.setColor(Color.RED.darker());
			g.drawRect(240,40,340,20);

		}else{
		g.setColor(Color.WHITE);
		g.setFont(new Font("Hobo Std",Font.PLAIN,20));
		g.drawString("-  Wave "+waveNumber+" -",WIDTH/2-30,30);
		}
	}
	
	private void gameDraw(){
		Graphics g2=this.getGraphics();
		g2.drawImage(image,0,0,null);
		g2.dispose();
	}
	
	private void createNewEnemies(){
		enemies.clear();
		Enemy e;
		if(waveNumber==1){
			for(int i=0;i<4;i++){
				enemies.add(new Enemy(1,1));
			}
		}
		if(waveNumber==2){
			for(int i=0;i<8;i++){
				enemies.add(new Enemy(1,1));
			}
		}
		if(waveNumber==3){
			for(int i=0;i<4;i++){
				enemies.add(new Enemy(1,2));
			}
		}
		if(waveNumber==4){
			for(int i=0;i<4;i++){
				enemies.add(new Enemy(1,1));
				enemies.add(new Enemy(1,2));
			}
		}			
		if(waveNumber==5){
			for(int i=0;i<4;i++){
				enemies.add(new Enemy(1,1));
				enemies.add(new Enemy(3,1));
				enemies.add(new Enemy(3,1));	
			}
		}	
		if(waveNumber==6){
			for(int i=0;i<4;i++){
				enemies.add(new Enemy(1,2));
				enemies.add(new Enemy(3,1));
			}
		}	
		if(waveNumber==7){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,2));
				enemies.add(new Enemy(3,2));
			}
		}		
		if(waveNumber==8){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(3,2));
			}
		}
		if(waveNumber==9){
			for(int i=0;i<4;i++){
				enemies.add(new Enemy(1,1));
				enemies.add(new Enemy(2,1));
				enemies.add(new Enemy(2,1));
				enemies.add(new Enemy(2,1));
			}
		}
		if(waveNumber==10){
			for(int i=0;i<4;i++){
				enemies.add(new Enemy(1,1));
				enemies.add(new Enemy(2,1));
				enemies.add(new Enemy(3,1));
				enemies.add(new Enemy(3,1));
			}	
		}
		if(waveNumber==11){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,1));
				enemies.add(new Enemy(3,1));
			}	
		}
		if(waveNumber==12){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,1));
				enemies.add(new Enemy(2,2));
				enemies.add(new Enemy(3,2));
				enemies.add(new Enemy(3,1));
			}	
		}
		if(waveNumber==13){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,1));
				enemies.add(new Enemy(2,2));
				enemies.add(new Enemy(3,3));				
			}	
		}
		if(waveNumber==14){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,1));
				enemies.add(new Enemy(3,2));
				enemies.add(new Enemy(2,3));				
			}	
		}
		if(waveNumber==15){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,2));
				enemies.add(new Enemy(2,1));
				enemies.add(new Enemy(3,3));			
			}	
		}	
		if(waveNumber==16){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,1));			
			}	
		}	
		if(waveNumber==17){
			for(int i=0;i<1;i++){
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,3));			
			}	
		}	
		if(waveNumber==18){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,1));
				enemies.add(new Enemy(1,2));
				enemies.add(new Enemy(3,3));
				enemies.add(new Enemy(2,3));
			}	
		}		
		if(waveNumber==19){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,1));
				enemies.add(new Enemy(2,1));
				enemies.add(new Enemy(3,2));
			}
		}
		if(waveNumber==20){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,2));
				enemies.add(new Enemy(3,1));
				enemies.add(new Enemy(3,1));
				enemies.add(new Enemy(1,2));
			}
		}
		if(waveNumber==21){
				for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,3));
				enemies.add(new Enemy(1,1));
				enemies.add(new Enemy(2,1));
			}
		}
		if(waveNumber==22){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,3));
				enemies.add(new Enemy(1,2));
				enemies.add(new Enemy(3,2));
			}
		}			
		if(waveNumber==23){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));	
				enemies.add(new Enemy(3,3));
				enemies.add(new Enemy(1,2));
				enemies.add(new Enemy(2,2));
			}
		}	
		if(waveNumber==24){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,3));
				enemies.add(new Enemy(3,2));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(1,1));
				enemies.add(new Enemy(2,1));

			}
		}	
		if(waveNumber==25){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,1));
				enemies.add(new Enemy(2,1));
				enemies.add(new Enemy(3,1));
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,3));
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,3));	
				enemies.add(new Enemy(3,1));				
			}	
		}
		if(waveNumber==26){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,1));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,1));
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,3));
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,3));	
				enemies.add(new Enemy(3,1));				
			}			
		}
		if(waveNumber==27){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,1));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,3));
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,3));
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,3));	
				enemies.add(new Enemy(3,1));				
			}			
		}
		if(waveNumber==28){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,2));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,2));
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,3));
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,3));	
				enemies.add(new Enemy(3,1));				
			}			
		}
		if(waveNumber==29){
			for(int i=0;i<2;i++){
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,2));
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,3));
				enemies.add(new Enemy(1,3));
				enemies.add(new Enemy(2,3));
				enemies.add(new Enemy(3,3));	
				enemies.add(new Enemy(2,3));				
			}			
		}		
		if(waveNumber==30){
			enemies.add(new Enemy(1,3));
			enemies.add(new Enemy(2,3));
			enemies.add(new Enemy(3,3));
			isBoss=true;
		}
	}
	public void restartSet(boolean b){restart=b;}
	public void keyTyped(KeyEvent key){}
public void keyPressed(KeyEvent key){
		int keyCode=key.getKeyCode();
		if(keyCode==KeyEvent.VK_ENTER){setStart(true);}
		if(keyCode==KeyEvent.VK_R){restartSet(true);}
		if(keyCode==KeyEvent.VK_LEFT){player.setLeft(true);}
		if(keyCode==KeyEvent.VK_RIGHT){player.setRight(true);}
		if(keyCode==KeyEvent.VK_UP){player.setUp(true);}
		if(keyCode==KeyEvent.VK_DOWN){player.setDown(true);}
		if(keyCode==KeyEvent.VK_SPACE){player.setFiring(true);}
		if(keyCode==KeyEvent.VK_P){pause=!pause;}
		if(keyCode==KeyEvent.VK_F){cheat=!cheat;}

	}

	public void keyReleased(KeyEvent key){
		int keyCode=key.getKeyCode();
		if(keyCode==KeyEvent.VK_ENTER){setStart(false);}
		if(keyCode==KeyEvent.VK_R){restartSet(false);}
		if(keyCode==KeyEvent.VK_LEFT){player.setLeft(false);}
		if(keyCode==KeyEvent.VK_RIGHT){player.setRight(false);}
		if(keyCode==KeyEvent.VK_UP){player.setUp(false);}
		if(keyCode==KeyEvent.VK_DOWN){player.setDown(false);}
		if(keyCode==KeyEvent.VK_SPACE){player.setFiring(false);}
	}
	
	public void mouseClicked(MouseEvent mouse){
	
	pause=false;

	}

	
	public void mousePressed(MouseEvent mouse){
		player.setFiring(true);

	}

	public void mouseReleased(MouseEvent mouse){
		player.setFiring(false);

		
	}
	public void mouseMoved(MouseEvent mouse){	
	player.setPlayer(mouse.getX(),mouse.getY());

	}

	public void mouseDragged(MouseEvent mouse){
		player.setPlayer(mouse.getX(),mouse.getY());
		player.setFiring(true);
	
	}
	
	public void mouseEntered(MouseEvent mouse){
	}
	
	public void mouseExited(MouseEvent mouse){
	
	}
	
}