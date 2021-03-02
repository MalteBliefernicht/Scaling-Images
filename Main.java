
package com.mycompany.testapp;

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.*;
import java.awt.image.BufferedImage;


public class Main extends JFrame {
    
    BufferedImage pic;
    JScrollPane scroll_panel;
    JPanel bar_panel;
    JPanel pic_panel;
    JLabel pic_label;
    JSlider slider;
    int[][] array;
    int width;
    int height;
    
    public Main() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(400,300);
        
        scroll_panel = new JScrollPane();
        
        bar_panel = new JPanel();
        bar_panel.setMinimumSize(new Dimension(300,60));
        bar_panel.setPreferredSize(new Dimension(300,60));
        bar_panel.setMaximumSize(new Dimension(300,60));
        
        slider = new JSlider();
        slider.setMinimum(-90);
        slider.setMaximum(90);
        slider.setMajorTickSpacing(1);
        Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
        table.put(0, new JLabel("0"));
        slider.setPaintLabels(true);
        slider.setLabelTable(table);
        slider.setSnapToTicks(true);
        slider.setValue(0);
        slider.setSize(300,70);
        bar_panel.add(slider);
        
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ev) {
                if (slider.getValueIsAdjusting() == false) {
                    if (slider.getValue() == 0) {
                        pic_label.setIcon(new ImageIcon(pic));
                    }
                    if (slider.getValue() < 0) {
                        zoomOut(slider.getValue()*-1);
                    }
                    if (slider.getValue() > 0) {
                        zoomIn(slider.getValue());
                    }
                }
            }
        });
        
        pic_panel = new JPanel(new GridBagLayout());
        pic_label = new JLabel();
        pic_panel.add(pic_label);
        scroll_panel.setViewportView(pic_panel);
        
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.add(scroll_panel);
        this.add(bar_panel);
        
        JMenuBar menu_bar = new JMenuBar();
        JMenu menu = new JMenu("Data");
        JMenuItem open_item = new JMenuItem("Open");
        menu.add(open_item);
        menu_bar.add(menu);
        this.setJMenuBar(menu_bar);
        
        open_item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                openItemClicked(ev);
            }
        });
    }
    
            
            
    private void openItemClicked(ActionEvent event) {
        
        FileFilter filter = new FileNameExtensionFilter("Pictures", "gif", "png", "jpg");
        JFileChooser chooser = new JFileChooser("C:/Users/Johnlocke/Desktop/Test");
        chooser.addChoosableFileFilter(filter);
        int output = chooser.showOpenDialog(this);
        
        if (output == JFileChooser.APPROVE_OPTION) {
            try {
                String path = chooser.getSelectedFile().getAbsolutePath();
                pic = ImageIO.read(new File(path));
                pic_label.setIcon(new ImageIcon(pic));
                
                pack();
                this.setLocationRelativeTo(null);
                
                width = pic.getWidth();
                height = pic.getHeight();
                array = new int[height][width];
                for (int row=0; row<height; row++) {
                    for (int col=0; col<width; col++) {
                        array[row][col] = pic.getRGB(col, row);
                    }
                }
                
            } catch(Exception e) {}    
        }
    }
    
    private void zoomOut(int value) {
        
        int new_height = height-((height*value)/100);
        int new_width = width-((width*value)/100);
        float pixel_size = (float) height/new_height;
        float pixel_area = pixel_size*pixel_size;
        
        BufferedImage new_pic = new BufferedImage(new_width, new_height, BufferedImage.TYPE_INT_RGB);
        
        for (int y=0; y<new_height; y++) {
            for (int x=0; x<new_width; x++) {
                List<Float> y_coords = new ArrayList<Float>();
                List<Float> x_coords = new ArrayList<Float>();
                ArrayList<ArrayList<Float>> proportions = new ArrayList<ArrayList<Float>>();
                
                int y_coord = y;
                int x_coord = x;
                float y_start = y_coord*pixel_size;
                float x_start = x_coord*pixel_size;
                float y_end = y_start+pixel_size;
                float x_end = x_start+pixel_size;
                
                float y_start_portion;
                if (Math.ceil(y_start) == y_start) {
                    y_start_portion = (y_start+1)-y_start;
                } else {
                    y_start_portion = (float) Math.ceil(y_start)-y_start;
                }
                float x_start_portion;
                if (Math.ceil(x_start) == x_start) {
                    x_start_portion = (x_start+1)-x_start;
                } else {
                    x_start_portion = (float) Math.ceil(x_start)-x_start;
                }
                float y_end_portion = y_end - (float) Math.floor(y_end);
                float x_end_portion = x_end - (float) Math.floor(x_end);
                
                y_coords.add(y_start_portion);
                double y_check = Math.floor(y_end)-Math.floor(y_start);
                if (y_check > 1) {
                    for (int i=0; i<(y_check-1); i++) {
                        y_coords.add(1f);
                    }
                }
                y_coords.add(y_end_portion);
                
                x_coords.add(x_start_portion);
                double x_check = Math.floor(x_end)-Math.floor(x_start);
                if (x_check > 1) {
                    for (int i=0; i<(x_check-1); i++) {
                        x_coords.add(1f);
                    }
                }
                x_coords.add(x_end_portion);
                
                float y_floor = (float) Math.floor(y_start);
                float x_floor = (float) Math.floor(x_start);
                for (int yy=0; yy<y_coords.size(); yy++) {
                    for (int xx=0; xx<x_coords.size(); xx++) {
                        ArrayList<Float> temp_list = new ArrayList<>();
                        if (y_floor+yy >= height && x_floor+xx >= width) {
                            temp_list.add((float)height-1);
                            temp_list.add((float)width-1);
                            temp_list.add(((y_coords.get(yy)*x_coords.get(xx))/(pixel_area/100))*(float)0.01);
                        } else if (y_floor+yy >= height) {
                            temp_list.add((float)height-1);
                            temp_list.add(x_floor+xx);
                            temp_list.add(((y_coords.get(yy)*x_coords.get(xx))/(pixel_area/100))*(float)0.01);
                        } else if (x_floor+xx >= width) {
                            temp_list.add(y_floor+yy);
                            temp_list.add((float)width-1);
                            temp_list.add(((y_coords.get(yy)*x_coords.get(xx))/(pixel_area/100))*(float)0.01);
                        } else {
                            temp_list.add(y_floor+yy);
                            temp_list.add(x_floor+xx);
                            temp_list.add(((y_coords.get(yy)*x_coords.get(xx))/(pixel_area/100))*(float)0.01);
                        }
                        proportions.add(temp_list);
                    }
                }
                int blue = 0;
                int green = 0;
                int red = 0;
                for (ArrayList pro:proportions) {
                    float y_cor = (float) pro.get(0);
                    float x_cor = (float) pro.get(1);
                    float prop = (float) pro.get(2);
                    int color = array[(int)y_cor][(int)x_cor];
                    blue += (color & 0xff)*prop;
                    green += ((color & 0xff00) >> 8)*prop;
                    red += ((color & 0xff0000) >> 16)*prop;
                }
                int rgb = red;
                rgb = (rgb << 8) + green;
                rgb = (rgb << 8) + blue;
                new_pic.setRGB(x,y,rgb);
            }
        }
        pic_label.setIcon(new ImageIcon(new_pic));
    }
    
    
    
    private void zoomIn(int value) {
        
        int new_height = height+((height*value)/100);
        int new_width = width+((width*value)/100);
        float pixel_size = (float) height/new_height;
        float pixel_area = pixel_size*pixel_size;

        BufferedImage new_pic = new BufferedImage(new_width, new_height, BufferedImage.TYPE_INT_RGB);
        
        for (int y=0; y<new_height; y++) {
            for (int x=0; x<new_width; x++) {
                List<Float> y_coords = new ArrayList<Float>();
                List<Float> x_coords = new ArrayList<Float>();
                ArrayList<ArrayList<Float>> proportions = new ArrayList<ArrayList<Float>>();
                
                int y_coord = y;
                int x_coord = x;
                float y_start = y_coord*pixel_size;
                float x_start = x_coord*pixel_size;
                float y_end = y_start+pixel_size;
                float x_end = x_start+pixel_size;
                
                float y_start_portion;
                float y_end_portion;
                if (Math.ceil(y_start) == Math.ceil(y_end)) {
                    y_start_portion = pixel_size;
                    y_coords.add(y_start_portion);
                } else {
                    y_start_portion = (float) Math.ceil(y_start)-y_start;
                    y_end_portion = y_end - (float) Math.floor(y_end);
                    y_coords.add(y_start_portion);
                    y_coords.add(y_end_portion);
                }
                float x_start_portion;
                float x_end_portion;
                if (Math.ceil(x_start) == Math.ceil(x_end)) {
                    x_start_portion = pixel_size;
                    x_coords.add(x_start_portion);
                } else {
                    x_start_portion = (float) Math.ceil(x_start)-x_start;
                    x_end_portion = x_end - (float) Math.floor(x_end);
                    x_coords.add(x_start_portion);
                    x_coords.add(x_end_portion);
                }
                float y_floor = (float) Math.floor(y_start);
                float x_floor = (float) Math.floor(x_start);
                for (int yy=0; yy<y_coords.size(); yy++) {
                    for (int xx=0; xx<x_coords.size(); xx++) {
                        ArrayList<Float> temp_list = new ArrayList<>();
                        if (y_floor+yy >= height && x_floor+xx >= width) {
                            temp_list.add((float)height-1);
                            temp_list.add((float)width-1);
                            temp_list.add(((y_coords.get(yy)*x_coords.get(xx))/(pixel_area/100))*(float)0.01);
                        } else if (y_floor+yy >= height) {
                            temp_list.add((float)height-1);
                            temp_list.add(x_floor+xx);
                            temp_list.add(((y_coords.get(yy)*x_coords.get(xx))/(pixel_area/100))*(float)0.01);
                        } else if (x_floor+xx >= width) {
                            temp_list.add(y_floor+yy);
                            temp_list.add((float)width-1);
                            temp_list.add(((y_coords.get(yy)*x_coords.get(xx))/(pixel_area/100))*(float)0.01);
                        } else {
                            temp_list.add(y_floor+yy);
                            temp_list.add(x_floor+xx);
                            temp_list.add(((y_coords.get(yy)*x_coords.get(xx))/(pixel_area/100))*(float)0.01);
                        }
                        proportions.add(temp_list);
                        
                    }
                }
                int blue = 0;
                int green = 0;
                int red = 0;
                for (ArrayList pro:proportions) {

                    float y_cor = (float) pro.get(0);
                    float x_cor = (float) pro.get(1);
                    float prop = (float) pro.get(2);

                    int color = array[(int)y_cor][(int)x_cor];
                    blue += (color & 0xff)*prop;
                    green += ((color & 0xff00) >> 8)*prop;
                    red += ((color & 0xff0000) >> 16)*prop;

                    
                }
                int rgb = red;
                rgb = (rgb << 8) + green;
                rgb = (rgb << 8) + blue;
                new_pic.setRGB(x,y,rgb);
            }
        }    
        pic_label.setIcon(new ImageIcon(new_pic));
    }
    
    
    public static void main(String[] args) {

        Main app = new Main();
        app.setLocationRelativeTo(null);
        app.setVisible(true);
    }  
}
