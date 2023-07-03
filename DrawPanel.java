/**
* Used to create all shapes and track mouse activities
*
* @author Hubert Woo
* @version July 4 2022
*/
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.GradientPaint; 

public class DrawPanel extends JPanel {
    private JLabel statusBar; 
    private Shape currentShape = null;
    private static ArrayList<Shape> shapes = new ArrayList<Shape>();
    private static ArrayList<Shape> saveShapes = new ArrayList<Shape>();
    
    //User's choices
    private static boolean filled;
    private static int shapeChoice;
    private static Color colorChoice = Color.LIGHT_GRAY;
    private static Color colorGradient1 = Color.LIGHT_GRAY;
    private static Color colorGradient2 = Color.LIGHT_GRAY; 
    private static boolean gradient = false;

    
    //DrawPanel Constructor, creates label to show mouse position and tracks mouse activities
    public DrawPanel( JLabel statusLabel ) {
        statusBar = statusLabel;
        setBackground( Color.WHITE ); 
        
        // Create and register listener for mouse and mouse motion events
        MouseEventListener drawPanelListener = new MouseEventListener(); 
        addMouseListener( drawPanelListener ); 
        addMouseMotionListener( drawPanelListener );       
    }

    //Takes in the user's color choice to draw shapes
    public static void importColor( Color userChoice){
        colorChoice = userChoice;
    }

    //Takes in the user's shape choice when drawing
    public static void importShape( int userChoice){
        shapeChoice = userChoice;
    } 

    //Takes in the user's choice of wanting either filled rectangles/ovals or just their outlines
    public static void importFilled( boolean userChoice){
        filled = userChoice;
    }

    //Calls repaint() method to update the canvas when editing shape arraylists
    public void update(){
        repaint();
    }

    //Allows the shape arraylist to be altered from the Options class
    public static void setShapes( ArrayList<Shape> change){
        shapes = change;
    }

    //Adds deleted shapes to a backup arraylist 
    public static void addSavedShapes( Shape newShape){
        saveShapes.add( newShape);
    }

    //Deletes the last drawn shape and saves it to a back up arraylist 
    public static void undoShape(){
        Shape lastDrawnShape = shapes.get( shapes.size()-1 );
        if( (lastDrawnShape instanceof EndFreeDraw) == true ){
            //Undo mechanic for freedraw
            int startFreeDraw = findStartOfFreeDraw( shapes );
            int endFreeDraw = shapes.size()-1;
            while( startFreeDraw <= endFreeDraw){
                saveShapes.add( shapes.get(startFreeDraw) );
                shapes.remove(startFreeDraw);
                endFreeDraw = shapes.size()-1;
            }   
        } 
        else{
            saveShapes.add( lastDrawnShape );
            shapes.remove( lastDrawnShape);
        }
    }

    //Allows a shape to be deleted from the shapes arraylist from a different class
    public static Shape getSpecificShape(int ShapePosition){
        return shapes.get(ShapePosition);
    }

    //Returns the number of shapes saved in the main "shapes" arraylist
    public static int getShapeSize(){
        return shapes.size();
    }

    //Gives other classes controlled access to the shapes arraylist
    public static ArrayList<Shape> getShapes(){
        return shapes;
    }

    //Gives other classes controlled access to the backup arraylist
    public static ArrayList<Shape> getSavedShapes(){
        return saveShapes;
    }

    //Redo mechanism used whenever the user presses the redo button
    public static void redoShape(){
        Shape lastDeletedShape = saveShapes.get( saveShapes.size()-1 );
        if( (lastDeletedShape instanceof EndFreeDraw) == true ){
            //Redo mechanic for freedraw
            int startFreeDraw = findStartOfFreeDraw( saveShapes );
            int endFreeDraw = saveShapes.size()-1;
            while( startFreeDraw <= endFreeDraw){
                shapes.add( saveShapes.get(startFreeDraw) );
                saveShapes.remove(startFreeDraw);
                endFreeDraw = saveShapes.size()-1;
            }   
        } 
        else{
            shapes.add( saveShapes.get( saveShapes.size()-1));
            saveShapes.remove( saveShapes.size()-1);
        }

    }

    //Mutator method for the backup arraylist
    public static void setSaveShapes( ArrayList<Shape> change){
        saveShapes = change;
    }

    //Delivers the user's first gradient color choice from the Options class
    public static void importColorGradient1(Color newColor){
        colorGradient1 = newColor;
    }
    //Delivers the user's second gradient color choice from the Options class 
    public static void importColorGradient2(Color newColor){
        colorGradient2 = newColor;
    }
    //Delivers the user's choice on whether they want a gradient design or not 
    public static void importGradient(boolean userGradient){
        gradient = userGradient;
    }

    //Finds the start of the last drawn freedraw stroke
    public static int findStartOfFreeDraw( ArrayList<Shape> shapes){
        for( int start = shapes.size()-1; start >= 0; start--){
            if( shapes.get(start) instanceof StartFreeDraw == true){
                return start;
            } 
        }
        return -1;
    }

    // Inner class to handle mouse events
    class MouseEventListener extends MouseAdapter {
        // Mouse press indicates a new shape has been started
        @Override
        public void mousePressed( MouseEvent event ) {
            if(shapeChoice == 0)
                currentShape = new Rectangle( event.getX(), event.getY(), event.getX(), event.getY(), colorChoice, gradient, filled, colorGradient1, colorGradient2);
            else if( shapeChoice == 1)
                currentShape = new Oval( event.getX(), event.getY(), event.getX(), event.getY(), colorChoice, gradient, filled, colorGradient1, colorGradient2);
            else if( shapeChoice == 2) 
                currentShape = new Line( event.getX(), event.getY(), event.getX(), event.getY(), colorChoice, gradient, colorGradient1, colorGradient2 );
            if( shapeChoice == 3){
                currentShape = new StartFreeDraw( event.getX(), event.getY(), event.getX(), event.getY(), colorChoice, gradient, colorGradient1, colorGradient2 );
                shapes.add(currentShape);
            }
                

            repaint();
        } 
        
        //Tracks whenever the user's mouse is released and creates a new shape
        @Override
        public void mouseReleased( MouseEvent event ) {
            currentShape.setX2( event.getX() );
            currentShape.setY2( event.getY() );
            currentShape.setColor( colorChoice );
            
            //Adds shape to arraylist to be drawn later
            shapes.add(currentShape);
            
            saveShapes = new ArrayList<Shape>();

            // Get ready for the next line to be drawn
            currentShape = null;
            repaint();            
        } 
        
        // As mouse is dragged, update coordinates of currentShape and statusBar
        @Override
        public void mouseDragged( MouseEvent event ) {
            if(shapeChoice == 3){
                //For Free Draw Mechanic
                //Continuously updates starting point to follow the cursor 
                currentShape.setX1( currentShape.getX2());
                currentShape.setY1( currentShape.getY2());              
            }
            currentShape.setX2( event.getX() );
            currentShape.setY2( event.getY() );
            statusBar.setText( String.format( "Mouse at (%d, %d)", 
                                             event.getX(), event.getY() ) );
            if( shapeChoice == 3){
                //Continuously creates a new line everytime the cursor is dragged
                currentShape = new EndFreeDraw( event.getX(), event.getY(), event.getX(), event.getY(), colorChoice, gradient, colorGradient1, colorGradient2 );
                shapes.add(currentShape);
            }                                           
            repaint();
        } 
        
        // As mouse is moved, just update the statusBar
        @Override
        public void mouseMoved( MouseEvent event ) {
            statusBar.setText( String.format( "Mouse at (%d, %d)", 
                                             event.getX(), event.getY() ) );                              
        } 
    } 
    
    // This method is called automatically by the JVM when the window needs to be (re)drawn.
    @Override
    public void paintComponent( Graphics g ) {
        super.paintComponent( g );
        // Call the draw() method for each Shape object in the array
        for( Shape value : shapes)
            value.draw(g);
        
        // If a line is in progress, draw it on top of all others
        if ( currentShape != null )
            currentShape.draw( g );
    } 
} 