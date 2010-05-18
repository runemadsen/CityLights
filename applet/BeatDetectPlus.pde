import processing.core.PApplet;
import ddf.minim.AudioBuffer;
import ddf.minim.Minim;

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
      
      int w = int(width/spect.avgSize());

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



