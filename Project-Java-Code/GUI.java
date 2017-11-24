
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
   private JLabel modeLabel;
   private JLabel statusLabel;
   
   // Panels containing buttons and images
   private JPanel buttonPanel;
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
      modeLabel = new JLabel("",JLabel.CENTER);
      camLabel1.setPreferredSize(new Dimension(475, 75));
      camLabel2.setPreferredSize(new Dimension(475, 75));
      modeLabel.setPreferredSize(new Dimension(650, 75));
      statusLabel = new JLabel("",JLabel.CENTER);        
      statusLabel.setSize(350,100);
      statusLabel.setText("Current Mode: Automatic"); 
      
      camLabel1.setText("Camera 1"); 
      camLabel1.setFont(camLabel1.getFont().deriveFont(18.0f));
      camLabel2.setText("Camera 2");
      camLabel2.setFont(camLabel2.getFont().deriveFont(18.0f));
      modeLabel.setText("Viewing Mode");
      modeLabel.setFont(modeLabel.getFont().deriveFont(24.0f));
      
      mainFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
      }); 
      
      // Create a panel with the buttons
      buttonPanel = new JPanel();
      buttonPanel.setLayout(new FlowLayout());
      buttonPanel.setPreferredSize(new Dimension(900, 75));
      
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
      mainFrame.add(modeLabel);
      mainFrame.add(buttonPanel);
      mainFrame.add(statusLabel);
      mainFrame.setVisible(true);  
   }
   
   public void runGUI(){
	   // Create the buttons
      JButton synchButton = new JButton("Synchronized");
      JButton autoButton = new JButton("Automatic");
      JButton asynchButton = new JButton("Asynchronized");

      // Set size of buttons
      synchButton.setPreferredSize(new Dimension(120, 50));
      autoButton.setPreferredSize(new Dimension(120, 50));
      asynchButton.setPreferredSize(new Dimension(120, 50));
      
      // Set action commands
      synchButton.setActionCommand("SYNCH");
      autoButton.setActionCommand("AUTO");
      asynchButton.setActionCommand("ASYNCH");

      // Create listeners for buttons
      synchButton.addActionListener(new ButtonClickListener()); 
      autoButton.addActionListener(new ButtonClickListener()); 
      asynchButton.addActionListener(new ButtonClickListener()); 

      // Add buttons to button panel
      buttonPanel.add(synchButton);
      buttonPanel.add(autoButton);
      buttonPanel.add(asynchButton);       

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
            statusLabel.setText("Current Mode: Synchronized");
            // Call monitor here to change mode
            mon.changeMode(0);
         } else if( command.equals( "AUTO" ) )  {
            statusLabel.setText("Current Mode: Automatic"); 
            // Call monitor here to change mode
            mon.changeMode(2);
         } else {
            statusLabel.setText("Current Mode: Asynchronized");
            // Call monitor here to change mode
            mon.changeMode(1);
         }  	
      }		
   }
}
