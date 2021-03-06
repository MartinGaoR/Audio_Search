package Player;
import Search.SearchDemo;
import Evaluation.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * Created by workshop on 9/18/2015.
 */
public class SoundEffectDemo extends JFrame implements ActionListener{

    JPanel contentPane;
    JButton openButton, searchButton, queryButton;
    JFileChooser fileChooser;
    JCheckBox check[] = new JCheckBox[4];
    JTextField box[] = new JTextField[4];
    JCheckBox distance[] = new JCheckBox[3];
    JButton precisionButton, recallButton;
    File queryAudio = null;
    int resultSize = 20;
    int flag[] = new int[4];
    double coff[] = new double[4];
    int choice = 0;
    double precision, recall;
    /**
     * If need, please replace the 'querySet' with specific path of test set of audio files in your PC.
     */
    String querySet = "data/input/";
    /**
     * Please Replace the 'basePath' with specific path of train set of audio files in your PC.
     */
    String basePath = "E:\\study\\study2015sem1\\CS2108\\Assignment 2\\data\\input\\train\\";


    JButton[] resultButton = new JButton[resultSize];
    JLabel [] resultLabels = new JLabel[resultSize];
    ArrayList<String> resultFiles = new ArrayList<String>();

    // Constructor
    public SoundEffectDemo() {
    	for(int i=0; i<4; i++){
    		flag[i] = 0;
    	}
    	for(int i=0; i<4; i++){
    		coff[i] = 0.25;
    	}
    	
        // Pre-load all the sound files
        queryAudio = null;
        SoundEffect.volume = SoundEffect.Volume.LOW;  // un-mute

        // Set up UI components;
        openButton = new JButton("Select an audio clip...");
        openButton.addActionListener(this);

        String tempName = "";

        queryButton = new JButton("Current Audio:"+tempName);
        queryButton.addActionListener(this);

        searchButton = new JButton("Search");
        searchButton.addActionListener(this);

        JPanel queryPanel = new JPanel();
        queryPanel.add(openButton);
        queryPanel.add(queryButton);
        queryPanel.add(searchButton);
        
        //selecting the coefficients panel
        JPanel selectPanel = new JPanel();
        selectPanel.setBounds(5, 25, 800, 40);
        selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.X_AXIS));
        //selectPanel.setLayout(new GridLayout(0,8));
        check[0] = new JCheckBox("Magnitude Spectrum");
        box[0] = new JTextField();
        box[0].setPreferredSize(new Dimension(5,5));
        box[0].setEditable(true);
        selectPanel.add(check[0]);
        selectPanel.add(box[0]);
        check[0].addActionListener(this);
        box[0].addActionListener(this);
        
        check[1] = new JCheckBox("Energy");
        box[1] = new JTextField();
        box[1].setPreferredSize(new Dimension(5,5));
        box[1].setEditable(true);
        selectPanel.add(check[1]);
        selectPanel.add(box[1]);
        check[1].addActionListener(this);
        box[1].addActionListener(this);
        
        check[2] = new JCheckBox("MFCC");
        box[2] = new JTextField();
        box[2].setPreferredSize(new Dimension(5,5));
        box[2].setEditable(true);
        selectPanel.add(check[2]);
        selectPanel.add(box[2]);
        check[2].addActionListener(this);
        box[2].addActionListener(this);
        
        check[3] = new JCheckBox("Zero Crossing");
        box[3] = new JTextField();
        box[0].setPreferredSize(new Dimension(5,5));
        box[3].setEditable(true);
        selectPanel.add(check[3]);
        selectPanel.add(box[3]);
        check[3].addActionListener(this);
        box[3].addActionListener(this);
        
        //distance panel
        JPanel distancePanel = new JPanel();
        distancePanel.setBounds(30, 60, 800, 30);
        distancePanel.setLayout(new BoxLayout(distancePanel, BoxLayout.X_AXIS));
        distance[0] = new JCheckBox("Cosine Distance");
        distancePanel.add(distance[0]);
        distance[1] = new JCheckBox("City Block Distance");
        distancePanel.add(distance[1]);
        distance[2] = new JCheckBox("Euclidean Distance");
        distancePanel.add(distance[2]);
        
        //result panel - for results
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new GridLayout(0, 4, 20, 20));

        for (int i = 0; i < resultLabels.length; i ++){
            resultLabels[i] = new JLabel();

            resultButton[i] = new JButton(resultLabels[i].getText());

            resultButton[i].addActionListener(this);

            resultButton[i].setVisible(false);
            resultPanel.add(resultLabels[i]);
            resultPanel.add(resultButton[i]);
        }
        
        resultPanel.setBorder(BorderFactory.createEmptyBorder(5,16,5,16));
        
        //top panel = query + select panel + distance panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(0,1));
        topPanel.add(queryPanel);
        topPanel.add(selectPanel);
        topPanel.add(distancePanel);
        
        //bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(0, 2, 10, 10));
        //bottomPanel.setBounds(30, 60, 800, 30);
        //bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        precisionButton = new JButton();
        recallButton = new JButton();
        bottomPanel.add(precisionButton);
        bottomPanel.add(recallButton);
        
        //content pane
        contentPane = (JPanel)this.getContentPane();
        setSize(800,900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane.add(topPanel, BorderLayout.PAGE_START);
        contentPane.add(resultPanel, BorderLayout.CENTER);
        contentPane.add(bottomPanel, BorderLayout.PAGE_END);

        contentPane.setVisible(true);
        setVisible(true);

    }

    public void actionPerformed(ActionEvent e){
    	if(e.getSource() == check[0]){
    		flag[0] = (flag[0] + 1) % 2;
    	}
    	if(e.getSource() == check[1]){
    		flag[1] = (flag[1] + 1) % 2;
    	}
    	if(e.getSource() == check[2]){
    		flag[2] = (flag[2] + 1) % 2;
    	}
    	if(e.getSource() == check[3]){
    		flag[3] = (flag[3] + 1) % 2;
    	}
    	
    	//file chooser
        if (e.getSource() == openButton){
            if (fileChooser == null) {
                fileChooser = new JFileChooser(querySet);

                fileChooser.addChoosableFileFilter(new AudioFilter());
                fileChooser.setAcceptAllFileFilterUsed(false);
            }
            int returnVal = fileChooser.showOpenDialog(SoundEffectDemo.this);

            if (returnVal == JFileChooser.APPROVE_OPTION){
                queryAudio = fileChooser.getSelectedFile();
            }

            fileChooser.setSelectedFile(null);

            queryButton.setText(queryAudio.getName());

            fileChooser.setSelectedFile(null);

        }else if (e.getSource() == searchButton){
        	for(int i=0; i<4; i++){
        		if(flag[i] == 0)
        			coff[i] = 0;
        		else
        			coff[i] = Double.parseDouble(box[i].getText());
        	}
        	
        	for(int i=0; i<3; i++){
        		if(distance[i].isSelected())
        			choice = i;
        	}
        	
        	System.out.println(coff[0]+ " " +coff[1]+" "+coff[2]+" "+coff[3]);
        	
            SearchDemo searchDemo = new SearchDemo(coff[0], coff[1], coff[2], coff[3], choice);
            resultFiles = searchDemo.resultList(queryAudio.getAbsolutePath());
            Precision p = new Precision();
            precision = p.result (resultFiles, queryAudio.getName());
            Recall r = new Recall();
            recall = r.result (resultFiles, queryAudio.getName());

            for (int i = 0; i < resultFiles.size(); i ++){
                resultLabels[i].setText(resultFiles.get(i));
                resultButton[i].setText(resultFiles.get(i));
                resultButton[i].setVisible(true);
            }
            
            precisionButton.setText("Precision: " + precision );
            recallButton.setText("Recall: " + recall );

        }else if (e.getSource() == queryButton){
            new SoundEffect(queryAudio.getAbsolutePath()).play();
        }else {
            for (int i = 0; i < resultSize; i ++){
                if (e.getSource() == resultButton[i]){
                    String filePath = basePath+resultFiles.get(i);
                    new SoundEffect(filePath).play();
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        new SoundEffectDemo();
    }
}
