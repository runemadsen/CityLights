import processing.core.*; 
import processing.xml.*; 

import processing.core.PApplet; 
import ddf.minim.AudioBuffer; 
import ddf.minim.Minim; 
import ddf.minim.*; 
import ddf.minim.analysis.*; 

import java.applet.*; 
import java.awt.*; 
import java.awt.image.*; 
import java.awt.event.*; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class citylights3 extends PApplet {

FrequencyImage frequencyImage;

public void setup()
{
   size(1024, 640);
   background(0);
   
   frequencyImage = new FrequencyImage("harrowdownhill.mp3", "city.jpg", "city2.jpg", this, 40, 8, 5);
   //frequencyImage.useVolume(true);
   //frequencyImage.setGraph(true);
   
   int sensitivity = 300;
   
   FrequencyShape theShape = new FrequencyShape(5, 8, 4, sensitivity, 10);
   theShape.addPos(0, 90);
   theShape.addPos(219, 90);
   theShape.addPos(223, 333);
   theShape.addPos(0, 370);
   frequencyImage.addRange(theShape);
   
   theShape = new FrequencyShape(14, 15, 2, sensitivity, 0);
   theShape.addPos(223, 99);
   theShape.addPos(300, 99);
   theShape.addPos(300, 282);
   theShape.addPos(223, 282);
   frequencyImage.addRange(theShape);
   
   theShape = new FrequencyShape(16, 16, 1, sensitivity, 0);
   theShape.addPos(827, 252);
   theShape.addPos(918, 243);
   theShape.addPos(917, 386);
   theShape.addPos(853, 369);
   theShape.addPos(852, 356);
   theShape.addPos(828, 342);
   frequencyImage.addRange(theShape);
   
   theShape = new FrequencyShape(18, 19, 2, sensitivity, 0);
   theShape.addPos(0, 370);
   theShape.addPos(190, 341);
   theShape.addPos(308, 352);
   theShape.addPos(312, 376);
   theShape.addPos(0, 500);
   frequencyImage.addRange(theShape);
   
   theShape = new FrequencyShape(20, 25, 3, sensitivity, 0);
   theShape.addPos(2, 504);
   theShape.addPos(360, 352);
   theShape.addPos(424, 377);
   theShape.addPos(285, 639);
   theShape.addPos(0, 639);
   frequencyImage.addRange(theShape);
   
   theShape = new FrequencyShape(29, 31, 3, sensitivity, 0);
   theShape.addPos(419, 144);
   theShape.addPos(498, 144);
   theShape.addPos(503, 350);
   theShape.addPos(394, 354);
   frequencyImage.addRange(theShape);
   
   theShape = new FrequencyShape(32, 32, 1, sensitivity, 0);
   theShape.addPos(513, 83);
   theShape.addPos(583, 77);
   theShape.addPos(586, 200);
   theShape.addPos(565, 200);
   theShape.addPos(553, 220);
   theShape.addPos(517, 229);
   frequencyImage.addRange(theShape);
   
   theShape = new FrequencyShape(33, 33, 1, sensitivity, 0);
   theShape.addPos(518, 237);
   theShape.addPos(630, 198);
   theShape.addPos(634, 333);
   theShape.addPos(516, 341);
   frequencyImage.addRange(theShape);
   
   theShape = new FrequencyShape(34, 34, 1, sensitivity, 0);
   theShape.addPos(524, 373);
   theShape.addPos(725, 292);
   theShape.addPos(760, 457);
   theShape.addPos(521, 415);
   frequencyImage.addRange(theShape);
   
   theShape = new FrequencyShape(35, 35, 1, sensitivity, 0);
   theShape.addPos(770, 129);
   theShape.addPos(865, 112);
   theShape.addPos(863, 233);
   theShape.addPos(770, 316);
   frequencyImage.addRange(theShape);
   
   frequencyImage.play();
}

public void draw()
{
   frequencyImage.update();
}





public class BeatDetectPlus
{
   private int	algorithm;
   private int	sampleRate;
   private int	timeSize;
   private int	valCnt;
   private float[] valGraph;
   private int	sensitivity;
   private int	insertAt;
   private boolean[] fIsOnset;
   private FFT spect;
   private float[][] feBuffer;
   private float[][] fdBuffer;
   private long[] fTimer;
   private float[] varGraph;
   private int varCnt;

   private int _logAverage1;
   private int _logAverage2;
   
   private int height2;

   /* Constructor
    ____________________________________________ */

   public BeatDetectPlus(int timeSize, float sampleRate, int logAverage1, int logAverage2)
   {
      _logAverage1 = logAverage1;
      _logAverage2 = logAverage2;		

      this.sampleRate = (int) sampleRate;
      this.timeSize = timeSize;
      initFEResources(logAverage1, logAverage2);
      initGraphs();
      sensitivity = 10;

      textFont(createFont("FFFCompactBold-8.vlw", 8));
      textAlign(CENTER);
      
      height2 = height - 45;
   }

   private void initGraphs()
   {
      valCnt = varCnt = 0;
      valGraph = new float[512];
      varGraph = new float[512];
   }

   private void initFEResources(int logAverage1, int logAverage2)
   {
      spect = new FFT(timeSize, sampleRate);
      spect.logAverages(logAverage1, logAverage2);
      int numAvg = spect.avgSize();
      fIsOnset = new boolean[numAvg];
      feBuffer = new float[numAvg][sampleRate / timeSize];
      fdBuffer = new float[numAvg][sampleRate / timeSize];
      fTimer = new long[numAvg];
      long start = System.currentTimeMillis();

      for (int i = 0; i < fTimer.length; i++)
      {
         fTimer[i] = start;
      }

      insertAt = 0;
   }

   /**
    * Analyze the samples in <code>ab</code>. This is a cumulative process,
    * so you must call this function every frame.
    * 
    * @param ab
    *           the AudioBuffer to analyze.
    */

   public void detect(AudioBuffer ab)
   {
      detect(ab.toArray());
   }

   /**
    * Analyze the samples in <code>buffer</code>. This is a cumulative
    * process, so you must call this function every frame.
    * 
    * @param buffer
    *           the buffer to analyze
    */

   public void detect(float[] buffer)
   {
      fEnergy(buffer);
   }

   /**
    * In frequency energy mode this returns true when a beat has been detect in
    * the <code>i<sup>th</sup></code> frequency band. In sound energy mode
    * this always returns false.
    * 
    * @param i
    *           the frequency band to query
    * @return true if a beat has been detected in the requested band
    */

   public boolean isOnset(int i)
   {
      return fIsOnset[i];
   }

   /**
    * In frequency energy mode this returns true if at least
    * <code>threshold</code> bands of the bands included in the range
    * <code>[low, high]</code> have registered a beat. In sound energy mode
    * this always returns false.
    * 
    * @param low
    *           the index of the lower band
    * @param high
    *           the index of the higher band
    * @param threshold
    *           the smallest number of bands in the range
    *           <code>[low, high]</code> that need to have registered a beat
    *           for this to return true
    * @return true if at least <code>threshold</code> bands of the bands
    *         included in the range <code>[low, high]</code> have registered a
    *         beat
    */

   public boolean isRange(int low, int high, int threshold)
   {
      int num = 0;

      for (int i = low; i < high + 1; i++)
      {
         if (isOnset(i))
         {
            num++;
         }
      }

      return num >= threshold;
   }

   /**
    * Sets the sensitivity of the algorithm. After a beat has been detected, the
    * algorithm will wait for <code>s</code> milliseconds before allowing
    * another beat to be reported. You can use this to dampen the algorithm if
    * it is giving too many false-positives. The default value is 10, which is
    * essentially no damping. If you try to set the sensitivity to a negative
    * value, an error will be reported and it will be set to 10 instead.
    * 
    * @param s
    *           the sensitivity in milliseconds
    */

   public void setSensitivity(int s)
   {
      if (s < 0)
      {
         Minim.error("BeatDetect: sensitivity cannot be less than zero. Defaulting to 10.");
         sensitivity = 10;
      }
      else
      {
         sensitivity = s;
      }
   }

   /**
    * Draws some debugging visuals in the passed PApplet. The visuals drawn when
    * in frequency energy mode are a good way to determine what values to use
    * with <code>inRange()</code> if the provided drum detecting functions
    * aren't what you need or aren't working well.
    * 
    * @param p
    *           the PApplet to draw in
    */

   public void drawGraph()
   {
      rectMode(CORNERS);
      
      //background(0);
      stroke(255);

      // draw the logarithmic averages
      //spect.forward(jingle.mix);
      
      int w = PApplet.parseInt(width/spect.avgSize());

      for(int i = 0; i < fTimer.length; i++)
      {
         int xPos = i*w;
         
         // Draw numbers
         fill(255);
         text(i, xPos + (w/2), height2 + 20);
         
         // check fill for beat 
         long clock = System.currentTimeMillis();

         if (clock - fTimer[i] < sensitivity)
         {
            noStroke();
            
            float h = PApplet.map(clock - fTimer[i], 0, sensitivity, 255, 0);
            fill(h);
            ellipse(xPos, height2 + 40, 15, 15);           
         }
       
         stroke(255);
         noFill();
         rect(xPos, height2, xPos + w, height2 - spect.getAvg(i));
      }
   }


   private void fEnergy(float[] in)
   {
      spect.forward(in);
      float instant, E, V, C, diff, dAvg, diff2;

      for (int i = 0; i < feBuffer.length; i++)
      {
         instant = spect.getAvg(i);
         E = average(feBuffer[i]);
         V = variance(feBuffer[i], E);
         C = (-0.0025714f * V) + 1.5142857f;
         diff = PApplet.max(instant - C * E, 0);
         dAvg = specAverage(fdBuffer[i]);
         diff2 = PApplet.max(diff - dAvg, 0);

         if (System.currentTimeMillis() - fTimer[i] < sensitivity)
         {
            fIsOnset[i] = false;
         }
         else if (diff2 > 0)
         {
            fIsOnset[i] = true;
            fTimer[i] = System.currentTimeMillis();
         }
         else
         {
            fIsOnset[i] = false;
         }

         feBuffer[i][insertAt] = instant;
         fdBuffer[i][insertAt] = diff;
      }

      insertAt++;

      if (insertAt == feBuffer[0].length)
      {
         insertAt = 0;
      }
   }

   private void pushVal(float v)
   {
      if (valCnt == valGraph.length)
      {
         valCnt = 0;
         valGraph = new float[valGraph.length];
      }

      valGraph[valCnt] = v;
      valCnt++;
   }

   private void pushVar(float v)
   {
      if (varCnt == varGraph.length)
      {
         varCnt = 0;
         varGraph = new float[varGraph.length];
      }

      varGraph[varCnt] = v;
      varCnt++;   
   }

   private float average(float[] arr)   
   {
      float avg = 0;

      for (int i = 0; i < arr.length; i++)
      {
         avg += arr[i];
      }

      avg /= arr.length;
      return avg;
   }

   private float specAverage(float[] arr)
   {
      float avg = 0;
      float num = 0;

      for (int i = 0; i < arr.length; i++)
      {
         if (arr[i] > 0)
         {
            avg += arr[i];
            num++;
         }
      }
      if (num > 0)
      {
         avg /= num;
      }
      return avg;
   }

   private float variance(float[] arr, float val)
   {
      float V = 0;

      for (int i = 0; i < arr.length; i++)
      {
         V += PApplet.pow(arr[i] - val, 2);
      }

      V /= arr.length;
      return V;
   }

   public FFT getFFT()
   {
      return spect;  
   }
}



class BeatListener implements AudioListener
{
  private BeatDetectPlus beat;
  private AudioPlayer source;
  
  BeatListener(BeatDetectPlus beat, AudioPlayer source)
  {
    this.source = source;
    this.source.addListener(this);
    this.beat = beat;
  }
  
  public void samples(float[] samps)
  {
    beat.detect(source.mix);
  }
  
  public void samples(float[] sampsL, float[] sampsR)
  {
    beat.detect(source.mix);
  }
}



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
   
   public void play()
   {
      _song.play();
   }
   
    /* Add Frequency Range
   ____________________________________________ */
   
   public void addRange(FrequencyShape frequencyShape)
   {
      _shapes.add(frequencyShape);
   }
   
   /* Update
   ____________________________________________ */
   
   public void update()
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
   
   public void stop()
   {

     _song.close();

     _minim.stop();

     //super.stop();
   }
   
   /* Getter / Setter
   ____________________________________________ */
   
   public void setGraph(boolean showGraph)
   {
      _showGraph = showGraph;  
   }
   
   public int getSensitivity()
   {
      return _sensitivity;  
   }
   
   public void setSensitivity(int sensitivity)
   {
      _sensitivity = sensitivity;
      
      _beat.setSensitivity(_sensitivity);
   }
   
   public void useVolume(boolean useVol)
   {
      _useVol = useVol;  
   }
}
class FrequencyShape
{
   /* Properties
    ____________________________________________ */

   private ArrayList _positions = new ArrayList();
   private int _lowBand;
   private int _highBand;
   private int _triggerNum;
   private int _sensitivity;
   private long _lastRange = 0;
   private float _minVolume = 0;

   /* Constructor
    ____________________________________________ */

   FrequencyShape(int lowBand, int highBand, int triggerNum, int sensitivity, float minVolume)
   {
      _sensitivity = sensitivity;
      _lowBand = lowBand;
      _highBand = highBand;
      _triggerNum = triggerNum;
      _minVolume = minVolume;
   }  

   /* Add position
    ____________________________________________ */

   public void addPos(float xPos, float yPos)
   {
      _positions.add(new PVector(xPos, yPos));  
   }

   /* Update
    ____________________________________________ */

   public int update(boolean isRange)
   {
      int colorNum = 255;

      if(isRange)
      {
         _lastRange = System.currentTimeMillis();
      }
      else
      {
         int difference = PApplet.parseInt(System.currentTimeMillis() - _lastRange);

         if(difference > _sensitivity)
         {
            colorNum = 0;
         }
         else
         {
            float percent = PApplet.parseFloat(difference) / PApplet.parseFloat(_sensitivity);

            colorNum = 255 - floor(percent * 255); 
         }
      }

      return colorNum;
   }

   /* Getter / Setter
    ____________________________________________ */

   public int getLowBand()
   {
      return _lowBand;  
   }

   public int getHighBand()
   {
      return _highBand;  
   }

   public int getTriggerNum()
   {
      return _triggerNum;  
   }

   public int getSize()
   {
      return _positions.size();  
   }

   public PVector getPos(int i)
   {
      return (PVector) _positions.get(i);  
   }
   
   public float getMinVolume()
   {
      return _minVolume;
   }
}


   static public void main(String args[]) {
      PApplet.main(new String[] { "--bgcolor=#FFFFFF", "citylights3" });
   }
}
