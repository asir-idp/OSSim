package edu.upc.fib.ossim.utils;

import java.awt.Dimension;

import javax.swing.Timer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import edu.upc.fib.ossim.template.Presenter;


/**
 * This panel manage simulations timing, controls: play, pause and stop, shows time and 
 * rate controller. 
 * It includes a main timer that generate time events and optionally a second timer (subtimer) faster    	
 * 
 * @author Alex Macia
 */
public class TimerPanel extends JPanel  { 
	private static final long serialVersionUID = 1L;
	
	private static int RATE_MIN = 500; // 0,5 seconds
	private static int RATE_MAX = 2000; // 2 seconds
	private static int INC = 500; // 0,5 seconds

	private JButton play;
	private JButton step;
	private JButton pause;
	private JButton stop;
	private JLabel ltime;
	private Timer timer;
	private Timer subtimer;
	private JSlider velocity;
	private int time;
	private int timesfaster; // Times faster second timer
	private int rate;
	private Presenter presenter;
	
	/**
	 * Constructs a TimerPanel, sets main timer and its initial rate 
	 * 
	 * @param presenter	event manager
	 * @param rate		timing rate
	 */
	public TimerPanel(Presenter presenter, int rate) { 
		super();
		this.presenter = presenter;
		this.rate = rate;
		time = 0;
		timer = new Timer(rate, presenter);
		timer.setActionCommand("timer");
		timer.stop(); 
		init();
	}

	/**
	 * Constructs a TimerPanel, sets two timers and their initial rates
	 * 
	 * @param presenter		event manager
	 * @param rate			main timing rate
	 * @param timesfaster	how much faster is second timing velocity  (rate/timesfaster)
	 */
	public TimerPanel(Presenter presenter, int rate, int timesfaster) { 
		super();
		this.presenter = presenter;
		this.rate = rate;
		this.timesfaster = timesfaster;
		time = 0;
		timer = new Timer(rate, presenter);
		timer.setActionCommand("timer");
		timer.stop(); 
		subtimer = new Timer(rate/timesfaster, presenter);
		subtimer.setActionCommand("subtimer");
		subtimer.stop(); 
		init();
	}

	private void init() {
		play = new JButton(Functions.getInstance().createImageIcon("play.png"));
		play.setPreferredSize(new Dimension(30, 30));
		play.setActionCommand("play");
		play.setToolTipText(Translation.getInstance().getLabel("all_40"));
		play.addActionListener(presenter);
		this.add(play);

		step = new JButton(Functions.getInstance().createImageIcon("step.png"));
		step.setPreferredSize(new Dimension(30, 30));
		step.setActionCommand("step");
		step.setToolTipText(Translation.getInstance().getLabel("all_41"));
		step.addActionListener(presenter);
		this.add(step);

		pause = new JButton(Functions.getInstance().createImageIcon("pause.png"));
		pause.setPreferredSize(new Dimension(30, 30));
		pause.setActionCommand("pause");
		pause.setToolTipText(Translation.getInstance().getLabel("all_42"));
		pause.addActionListener(presenter);
		this.add(pause);

		stop = new JButton(Functions.getInstance().createImageIcon("stop.png"));
		stop.setPreferredSize(new Dimension(30, 30));
		stop.setActionCommand("stop");
		stop.setToolTipText(Translation.getInstance().getLabel("all_43"));
		stop.addActionListener(presenter);
		this.add(stop);

		velocity = new JSlider(RATE_MIN, RATE_MAX, rate);
		velocity.setPreferredSize(new Dimension(100, 30));
		velocity.setMinorTickSpacing(INC);
		velocity.setMajorTickSpacing(INC);
		velocity.setSnapToTicks(true);
		velocity.setPaintTicks(true);
		velocity.setToolTipText(Translation.getInstance().getLabel("all_44"));
		velocity.addChangeListener(presenter);
		this.add(velocity);
		
		JLabel espai = new JLabel("  ");
		this.add(espai);

		ltime = new JLabel(Translation.getInstance().getLabel("all_45") + " 0 " + Translation.getInstance().getLabel("all_46"));
		this.add(ltime);
	}

	/**
	 * Starts timer and optionally second timer, disable velocity control     
	 */
	public void play() {
		velocity.setEnabled(false);
		timer.start(); 
		if (subtimer != null) subtimer.start();
	}

	/**
	 * Stops timer and optionally second timer, restarts time parameters and enables velocity control     
	 */
	public void stop() {
		timer.stop();
		if (subtimer != null) subtimer.stop();
		time = 0;
		ltime.setText(Translation.getInstance().getLabel("all_45") + " " + this.time + " " + Translation.getInstance().getLabel("all_46"));
		velocity.setEnabled(true);
	}

	/**
	 * Pause timer and optionally second timer
	 */
	public void pause() {
		timer.stop();
		if (subtimer != null) subtimer.stop();
	}
	
	/**
	 * Timer event, increase main timing
	 */
	public void timer() {
		time++;
		ltime.setText(Translation.getInstance().getLabel("all_45") + " " + this.time + " " + Translation.getInstance().getLabel("all_46"));
	}

	/**
	 * Getter of main time  
	 * 
	 * @return main time 
	 */
	public int getTime() {
		return this.time;
	}

	/**
	 * Getter of subtiming rate  
	 * 
	 * @return subtiming rate 
	 */
	public int getTimesfaster() {
		return timesfaster;
	}

	/**
	 * Is simulation running  
	 * 
	 * @return simulation is running
	 */
	public boolean isRunning() {
		return timer.isRunning();
	}
	
	/**
	 * Sets main and optionally second timer rate     
	 * 
	 * @param rate	timer rate
	 */
	public void setDelay(int rate) {
		this.rate = rate;
		timer.setDelay(rate);
		if (subtimer != null) subtimer.setDelay(rate/timesfaster);
	}
	
	/**
	 * Translates tooltips and time string to current session language 
	 */
	public void updateLabels() {
		play.setToolTipText(Translation.getInstance().getLabel("all_40"));
		step.setToolTipText(Translation.getInstance().getLabel("all_41"));
		pause.setToolTipText(Translation.getInstance().getLabel("all_42"));
		stop.setToolTipText(Translation.getInstance().getLabel("all_43"));
		velocity.setToolTipText(Translation.getInstance().getLabel("all_44"));
		ltime.setText(Translation.getInstance().getLabel("all_45") + " " + this.time + " " + Translation.getInstance().getLabel("all_46"));
	}
} 