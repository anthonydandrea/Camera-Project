
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class GUI {
	// Main GUI panel
   private JFrame mainFrame;
   
   private monitor mon;
   
   // Text labels
   private JLabel camLabel1;
   private JLabel camLabel2;
   private JLabel viewModeLabel;
   private JLabel camModeLabel;
   private JLabel viewStatusLabel;
   private JLabel camStatusLabel;
   public  JLabel delayLabel;
   
   // Panels containing buttons and images
   private JPanel viewButtonPanel;
   private JPanel camButtonPanel;
   public ImagePanel imagePanel1;
   public ImagePanel imagePanel2;


   public GUI(){
      prepareGUI();
   }

   private void prepareGUI(){
	   // Create the main frame
      mainFrame = new JFrame("Security Camera");
      mainFrame.setSize(1000,700);
      mainFrame.setLayout(new FlowLayout());
      
      // Create the labels
      camLabel1 = new JLabel("",JLabel.CENTER );
      camLabel2 = new JLabel("",JLabel.CENTER );
      viewModeLabel = new JLabel("",JLabel.CENTER);
      camModeLabel = new JLabel("", JLabel.CENTER);
       delayLabel = new JLabel("", JLabel.CENTER);
      camLabel1.setPreferredSize(new Dimension(475, 15));
      camLabel2.setPreferredSize(new Dimension(475, 15));
      viewModeLabel.setPreferredSize(new Dimension(900, 30));
      camModeLabel.setPreferredSize(new Dimension(900, 30));
       delayLabel.setPreferredSize(new Dimension(900, 30));
      viewStatusLabel = new JLabel("",JLabel.CENTER);        
      viewStatusLabel.setSize(800,100);
      viewStatusLabel.setText("Current Viewing Mode: Automatic"); 
      camStatusLabel = new JLabel("", JLabel.CENTER);
      camStatusLabel.setSize(800, 100);
      camStatusLabel.setText("Current Camera Mode: Automatic");
      
      camLabel1.setText("Camera 1"); 
      camLabel1.setFont(camLabel1.getFont().deriveFont(14.0f));
      camLabel2.setText("Camera 2");
      camLabel2.setFont(camLabel2.getFont().deriveFont(14.0f));
      viewModeLabel.setText("Viewing Mode");
      viewModeLabel.setFont(viewModeLabel.getFont().deriveFont(18.0f));
      camModeLabel.setText("Camera Mode");
      camModeLabel.setFont(camModeLabel.getFont().deriveFont(18.0f));
      delayLabel.setText("Delay current time = 0");
      delayLabel.setFont(camModeLabel.getFont().deriveFont(12.0f));
       
      mainFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
      }); 
      
      // Create a panel with the buttons
      viewButtonPanel = new JPanel();
      viewButtonPanel.setLayout(new FlowLayout());
      viewButtonPanel.setPreferredSize(new Dimension(900, 55));
      
      camButtonPanel = new JPanel();
      camButtonPanel.setLayout(new FlowLayout());
      camButtonPanel.setPreferredSize(new Dimension(900, 55));
      
      // Create a the panels showing the images
      imagePanel1 = new ImagePanel();
      imagePanel2 = new ImagePanel();
      imagePanel1.setPreferredSize(new Dimension(400,400));
      imagePanel2.setPreferredSize(new Dimension(400,400));
      
      
      // Add all components to the GUI
      mainFrame.add(camLabel1);
      mainFrame.add(camLabel2);
      mainFrame.add(imagePanel1);
      mainFrame.add(imagePanel2);
    mainFrame.add(delayLabel);
      mainFrame.add(viewModeLabel);
      mainFrame.add(viewButtonPanel);
      mainFrame.add(viewStatusLabel);
      mainFrame.add(camModeLabel);
      mainFrame.add(camButtonPanel);
      mainFrame.add(camStatusLabel);
      mainFrame.setVisible(true);  
   }
   
   public void runGUI(){
	   // Create the buttons
      JButton synchButton = new JButton("Synchronized");
      JButton autoViewButton = new JButton("Automatic");
      JButton asynchButton = new JButton("Asynchronized");
      JButton idleButton = new JButton("Idle");
      JButton autoCamButton = new JButton("Automatic");
      JButton movieButton = new JButton("Movie");
      
      // Set size of buttons
      synchButton.setPreferredSize(new Dimension(120, 50));
      autoViewButton.setPreferredSize(new Dimension(120, 50));
      asynchButton.setPreferredSize(new Dimension(120, 50));
      idleButton.setPreferredSize(new Dimension(120, 50));
      autoCamButton.setPreferredSize(new Dimension(120, 50));
      movieButton.setPreferredSize(new Dimension(120, 50));
      
      // Set action commands
      synchButton.setActionCommand("SYNCH");
      autoViewButton.setActionCommand("AUTOVIEW");
      asynchButton.setActionCommand("ASYNCH");
      idleButton.setActionCommand("IDLE");
      autoCamButton.setActionCommand("AUTOCAM");
      movieButton.setActionCommand("MOVIE");

      // Create listeners for buttons
      synchButton.addActionListener(new ButtonClickListener()); 
      autoViewButton.addActionListener(new ButtonClickListener()); 
      asynchButton.addActionListener(new ButtonClickListener()); 
      idleButton.addActionListener(new ButtonClickListener());
      autoCamButton.addActionListener(new ButtonClickListener());
      movieButton.addActionListener(new ButtonClickListener());

      // Add buttons to button panel
      viewButtonPanel.add(synchButton);
      viewButtonPanel.add(autoViewButton);
      viewButtonPanel.add(asynchButton);       
      camButtonPanel.add(idleButton);
      camButtonPanel.add(autoCamButton);
      camButtonPanel.add(movieButton);
      
      mainFrame.setVisible(true);  
   }
   
   public void addMonitor(monitor m) {
	   mon = m;
   }
  
   
   private class ButtonClickListener implements ActionListener{
      public void actionPerformed(ActionEvent e) {
         String command = e.getActionCommand();  
         
         // Determine action to take based on the command
         if( command.equals( "SYNCH" ))  {
            viewStatusLabel.setText("Current Viewing Mode: Synchronized");
            // Synchronous Mode
            mon.changeMode(0, 0);
         } else if( command.equals( "AUTOVIEW" ) )  {
            viewStatusLabel.setText("Current Viewing Mode: Automatic"); 
            // Automatic viewing mode
            mon.changeMode(2, 0);
         } else if (command.equals("ASYNCH")){
            viewStatusLabel.setText("Current Viewing Mode: Asynchronized");
            // Asynchronous Mode
            mon.changeMode(1, 0);
         }  	else if (command.equals("IDLE")) {
        	 	// Idle Mode
        	 	camStatusLabel.setText("Current Camera Mode: Idle");
        	 	mon.changeMode(0, 1);
         } else if (command.equals("AUTOCAM")) {
        	 	// Automatic camera Mode
        	 	camStatusLabel.setText("Current Camera Mode: Automatic");
        	 	mon.changeMode(2,  1);
         } else if (command.equals("MOVIE")) {
        	 	// Movie mode
        	 	camStatusLabel.setText("Current Camera Mode: Movie");
        	 	mon.changeMode(1, 1);
         }
      }		
   }
}
