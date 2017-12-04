
import java.awt.event.*;
import java.awt.*;

import javax.swing.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;


public class MainWindow {
	private Monitor monitor;
	private JFrame frame;
	private ImagePanel imagePanel;
	private InfoPanel infoPanel;
	private JButton asyncButton;
	private JButton autoButton;
	private JButton syncButton;
	
	public MainWindow(Monitor m) {
		monitor = m;
		initialize();
	}
        public MainWindow() {
                initialize();
        }
	
	private void initialize() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				WindowAdapter listener = new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						System.exit(0);
					}
				};
				frame.addWindowListener(listener);

				Container contentPane = frame.getContentPane();
				contentPane.setLayout(new BoxLayout(contentPane,
						BoxLayout.Y_AXIS));

				 imagePanel = new ImagePanel();
				infoPanel = new InfoPanel();
				monitor.setResources(imagePanel, infoPanel);
				contentPane.add(imagePanel);
				contentPane.add(infoPanel);

				contentPane.add(new JSeparator());

				asyncButton = new JButton("Prev");
				asyncButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						//monitor.previous();
					}
				});

                       	        autoButton = new JButton("Auto");
				autoButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
					}
				});



				syncButton = new JButton("Next");
				syncButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						//monitor.next();
					}
				});
				
				JPanel bottomPane = new JPanel();
				bottomPane.setLayout(new FlowLayout());
				bottomPane.add(asyncButton);
				bottomPane.add(autoButton);
				bottomPane.add(syncButton);
				contentPane.add(bottomPane);

				frame.setPreferredSize(new Dimension(410,550));
				frame.setLocationRelativeTo(null);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
}
