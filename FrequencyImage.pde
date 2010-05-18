import ddf.minim.*;
import ddf.minim.analysis.*;

class FrequencyImage
{
   /* Properties
   ____________________________________________ */
   
   Minim _minim;
   AudioPlayer _song;
   BeatDetectPlus _beat;
   BeatListener _listener;
   
   String _songName;
   PImage _bottomImage;
   PImage _topImage;
   PGraphics _mask;
   PApplet _parent;
   int _sensitivity;
   boolean _useVol = false;
   
   boolean _showGraph = false;
   
   ArrayList _shapes = new ArrayList();
   
   /* Constructor
   ____________________________________________ */
   
   FrequencyImage(String songName, String bottomImage, String topImage, PApplet parent, int lowestFreq, int bandsPrOctave, int sensitivity)
   {
      _songName = songName;
      _parent = parent;
      _sensitivity = sensitivity;
      
      _minim = new Minim(parent);
  
      _song = _minim.loadFile(_songName, 2048);
    
      _beat = new BeatDetectPlus(_song.bufferSize(), _song.sampleRate(), lowestFreq, bandsPrOctave);
      _beat.setSensitivity(_sensitivity);
    
      _listener = new BeatListener(_beat, _song);  
     
      _mask = createGraphics(1024, 640, JAVA2D);
   
      _bottomImage = loadImage(bottomImage);
      _topImage = loadImage(topImage);
   }  
   
   void play()
   {
      _song.play();
   }
   
    /* Add Frequency Range
   ____________________________________________ */
   
   void addRange(FrequencyShape frequencyShape)
   {
      _shapes.add(frequencyShape);
   }
   
   /* Update
   ____________________________________________ */
   
   void update()
   {
       background(0);
  
      _mask.beginDraw();
      _mask.background(0);
      _mask.noStroke();
      
      for(int i = 0; i < _shapes.size(); i++)
      {
         FrequencyShape theShape = (FrequencyShape) _shapes.get(i);
         
         int fillColor = 0;
         
         // color by volume
         if(_useVol)
         {
            float amplitude = _beat.getFFT().calcAvg(theShape.getLowBand(), theShape.getHighBand());
            fillColor = floor(map(amplitude, 0, 100, 0, 255));
         }
          // color by beatdetect
         else
         {
            boolean isRange = _beat.isRange(theShape.getLowBand(), theShape.getHighBand(), theShape.getTriggerNum());
            
            // check is volume is over minVolume
            float amplitude = _beat.getFFT().calcAvg(theShape.getLowBand(), theShape.getHighBand());
  
            if(amplitude < theShape.getMinVolume())
            {
              isRange = false;   
            }
            
            fillColor = theShape.update(isRange);
         }
         
         if(fillColor > 0)
         { 
            _mask.fill(fillColor);
            _mask.beginShape();
               
            for(int n = 0; n < theShape.getSize(); n++)
            {
               _mask.vertex(theShape.getPos(n).x, theShape.getPos(n).y);
            }
            
            _mask.endShape();
         }
      }
      
      _mask.endDraw();
      _mask.loadPixels();
   
      _bottomImage.mask(_mask.pixels);
      
      image(_topImage, 0, 0);
      image(_bottomImage, 0, 0);
      //image(_mask, 0, 0);
        
      if(_showGraph)
      {
         _beat.drawGraph();
      }  
   }
   
   void stop()
   {

     _song.close();

     _minim.stop();

     //super.stop();
   }
   
   /* Getter / Setter
   ____________________________________________ */
   
   void setGraph(boolean showGraph)
   {
      _showGraph = showGraph;  
   }
   
   int getSensitivity()
   {
      return _sensitivity;  
   }
   
   void setSensitivity(int sensitivity)
   {
      _sensitivity = sensitivity;
      
      _beat.setSensitivity(_sensitivity);
   }
   
   void useVolume(boolean useVol)
   {
      _useVol = useVol;  
   }
}
