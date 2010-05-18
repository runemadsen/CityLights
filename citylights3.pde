FrequencyImage frequencyImage;

void setup()
{
   size(1024, 640);
   //size(screen.width, screen.height);
   background(0);
   
   frequencyImage = new FrequencyImage("harrowdownhill.mp3", "city.jpg", "city2.jpg", this, 40, 8, 10);
   //frequencyImage.useVolume(true);
   frequencyImage.setGraph(true);
   
   int sensitivity = 300;
   
   FrequencyShape theShape = new FrequencyShape(5, 8, 3, sensitivity, 2);
   theShape.addPos(0, 90);
   theShape.addPos(219, 90);
   theShape.addPos(223, 333);
   theShape.addPos(0, 370);
   frequencyImage.addRange(theShape);
   
   theShape = new FrequencyShape(14, 15, 2, sensitivity, 10);
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

void draw()
{
   frequencyImage.update();
}

