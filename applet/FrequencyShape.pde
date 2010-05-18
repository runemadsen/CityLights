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

   void addPos(float xPos, float yPos)
   {
      _positions.add(new PVector(xPos, yPos));  
   }

   /* Update
    ____________________________________________ */

   int update(boolean isRange)
   {
      int colorNum = 255;

      if(isRange)
      {
         _lastRange = System.currentTimeMillis();
      }
      else
      {
         int difference = int(System.currentTimeMillis() - _lastRange);

         if(difference > _sensitivity)
         {
            colorNum = 0;
         }
         else
         {
            float percent = float(difference) / float(_sensitivity);

            colorNum = 255 - floor(percent * 255); 
         }
      }

      return colorNum;
   }

   /* Getter / Setter
    ____________________________________________ */

   int getLowBand()
   {
      return _lowBand;  
   }

   int getHighBand()
   {
      return _highBand;  
   }

   int getTriggerNum()
   {
      return _triggerNum;  
   }

   int getSize()
   {
      return _positions.size();  
   }

   PVector getPos(int i)
   {
      return (PVector) _positions.get(i);  
   }
   
   float getMinVolume()
   {
      return _minVolume;
   }
}

