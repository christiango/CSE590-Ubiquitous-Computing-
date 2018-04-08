package go.christian.steptracker;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SensorTests {
    private int runStepCounterOnData(String data) {
        List<Accelerometer.Event> events = SensorDataExporter.importFromString(data);

        StepCounter stepCounter = StepCounterFactory.GetStepCounter();

        for(Accelerometer.Event event: events) {
            float [] valuesArray = new float[]  {event.x, event.y, event.z};
            stepCounter.addData(valuesArray);
        }

        return stepCounter.getStepCount();
    }

  @Test
  public void bigSteps_4() {
    String data =
        "timestamp,x,y,z\n840201266495290,-0.21068975,0.7469909,8.283937\n840201331506228,-0.31603462,-0.17238252,8.446744\n840201396484353,-0.91937345,0.61291564,7.9966335\n840201461493728,0.19153613,-0.02873042,8.025364\n840201526503103,-3.5529952,0.31603462,11.597513\n840201591512478,-0.90021986,-0.90021986,9.184157\n840201656490603,-1.9057846,-1.0821792,9.998186\n840201721501540,-0.8331822,-1.7621324,8.734048\n840201786510915,-3.169923,-0.7757214,10.103531\n840201851489040,-2.873042,-1.8674773,10.984597\n840201916498415,-5.171476,-0.019153614,9.32781\n840201981507790,-5.1810527,-1.541866,10.611102\n840202046485915,-7.795521,-0.47884035,9.433155\n840202111495290,-5.4779334,-2.2697031,9.279925\n840202176504665,-6.2728086,-1.5705963,7.393295\n840202241484353,-4.1946416,-2.72939,8.724471\n840202306493728,-6.8569937,-1.3790601,8.389283\n840202371503103,-7.192182,-1.292869,6.090849\n840202436512478,-7.4603324,-1.7238252,6.9527617\n840202501490603,-8.389283,-1.091756,6.5505357\n840202566499978,-5.6982,-2.432509,6.5026517\n840202631509353,-8.111555,-1.436521,5.9759274\n840202696489040,-8.762778,-1.9057846,3.016694\n840202761498415,-8.973468,-2.566584,5.085284\n840202826507790,-8.283937,-2.3750482,4.098873\n840202891485915,-11.81778,-0.63206923,4.2042184\n840202956495290,-7.881712,-2.9113493,-0.11492168\n840203021504665,-10.534488,-3.074155,-0.94810385\n840203086514040,-7.6710224,-4.280833,-0.7278373\n840203151493728,-6.6654577,-5.66947,-2.767697\n840203216503103,-11.520899,-3.5051112,-2.4133554\n840203281512478,-4.807557,-7.2209125,-2.5091233\n840203346490603,-10.94629,-5.0757074,-1.5131354\n840203411499978,-5.2480903,-8.954314,-2.0973208\n840203476509353,-9.643845,-7.8050976,-3.217807\n840203541487478,-5.995081,-7.28795,-7.1730285\n840203606498415,-6.205771,-6.349423,-5.0661306\n840203671507790,-4.5681367,-6.3111157,-1.6184803\n840203736485915,-2.6431987,-4.1276035,-1.6567876\n840203801495290,-0.36391866,-4.1659107,2.0111294\n840203866504665,-1.13964,-30.004135,4.2425256\n840203931515603,-3.3614593,-6.5218053,-0.25857377\n840203996493728,5.3821654,-9.251195,2.719813\n840204061503103,2.6623523,-9.883265,-0.61291564\n840204126512478,1.2066777,-9.404425,1.4460979\n840204191490603,1.3694834,-9.662998,0.49799395\n840204256499978,1.4460979,-9.5385,1.1683705\n840204321509353,1.2641385,-9.519346,0.78529817\n840204386487478,1.6663644,-9.883265,-0.22984336\n840204451496853,2.1260512,-9.662998,-0.16280572\n840204516507790,2.0590134,-9.471462,0.16280572\n840204581485915,1.436521,-9.490616,0.39264908\n840204646495290,0.63206923,-9.634268,0.5650316\n840204711504665,-0.24899697,-9.969456,0.49799395\n840204776514040,-0.17238252,-9.701305,0.63206923\n840204841492165,1.5993267,-9.758766,1.0342951\n840204906501540,-0.38307226,-9.270349,0.62249243\n840204971512478,-0.06703765,-9.509769,-0.06703765\n840205036490603,-0.41180268,-9.423578,0.545878\n840205101499978,-0.59376204,-9.471462,1.0726024\n840205166509353,-0.45010993,-9.385271,2.4612393\n840205231487478,0.4405331,-10.228029,2.1068976\n840205296496853,0.58418524,-10.965444,2.432509\n840205361507790,-2.0207062,-13.560759,4.7500963\n840205426485915,1.1300632,-14.001291,1.2737153\n840205491493728,-0.93852705,-11.166556,2.2888567\n840205556504665,1.7908629,-11.425131,1.7429788\n840205621514040,3.9360676,-12.612655,-0.6895301\n840205686492165,1.3215994,-9.615114,1.2641385\n840205751503103,0.41180268,-8.408437,-1.0534488\n840205816512478,0.33518824,-7.2687964,-0.58418524\n840205881490603,0.8714894,-5.085284,-1.0247183\n840205946498415,2.1547816,-6.0716953,-3.9743748\n840206011509353,4.2521024,-9.021352,-5.631162\n840206076487478,4.1659107,-8.341399,-4.865018\n840206141496853,8.169016,-9.940725,-9.940725\n840206206506228,-5.6982,-5.171476,17.24783\n840206271484353,6.244078,-5.755661,0.6703765\n840206336493728,2.5761611,-9.270349,-3.7732618\n840206401504665,4.7022123,-10.927136,-0.5363012\n840206466514040,2.7102363,-9.643845,-0.1436521\n840206531492165,0.5650316,-10.342952,-0.48841715\n840206596501540,-0.9672575,-8.322245,-1.8483237\n840206661510915,-0.5554548,-8.743625,0.30645782\n840206726489040,0.12449849,-8.925584,-0.37349546\n840206791498415,0.5267244,-10.0269165,1.7908629\n840206856509353,-0.7086837,-11.032481,4.462792\n840206921487478,0.1340753,-11.434708,2.528277\n840206986496853,-0.545878,-11.013328,2.6623523\n840207051506228,1.6184803,-14.690822,3.6104562\n840207116484353,5.985504,-13.627796,0.39264908\n840207181493728,4.1659107,-14.058752,1.1204864\n840207246504665,2.327164,-11.319786,2.825158\n840207311514040,-0.641646,-7.192182,0.076614454\n840207376492165,-0.6799533,-4.7022123,-0.095768064\n840207441501540,-0.9864111,-3.4572272,1.733402\n840207506510915,4.711789,-5.707777,-6.14831\n840207571489040,6.540959,-7.4986396,-4.4819455\n840207636498415,6.1100025,-6.579266,-6.5218053\n840207701507790,23.27164,-19.632454,-11.568783\n840207766487478,0.59376204,4.309563,-5.209783\n840207831496853,6.4260373,-19.182344,-2.27928\n840207896506228,3.6870706,-7.297527,-3.5529952\n840207961484353,4.711789,-9.710882,-4.357447\n840208026493728,5.6790466,-6.493075,-2.1452048\n840208091504665,4.960786,-8.6574335,-2.8921957\n840208156514040,1.9919758,-9.040505,-1.1587936\n840208221490603,1.2066777,-10.879252,-0.93852705\n840208286501540,0.30645782,-9.921572,-2.4899697\n840208351510915,0.48841715,-9.595961,-2.2218192\n840208416489040,1.2162545,-9.442732,-0.4405331\n840208481498415,2.0015526,-10.266336,-0.49799395\n840208546507790,1.2258313,-9.222465,0.92895025\n840208611487478,1.9057846,-9.203311,1.4748282\n840208676496853,3.9552212,-11.013328,1.2641385\n840208741506228,3.265691,-10.237606,-1.4652514\n840208806484353,2.6240451,-6.9240313,2.135628\n840208871493728,4.6926355,-9.059659,-1.0055647\n840208936504665,3.41892,-10.936713,-3.0933087\n840209001512478,1.6280571,-9.078813,0.047884032\n840209066490603,2.336741,-9.126697,-0.35434186\n840209131501540,4.3861775,-10.869676,-1.9632454\n840209196510915,2.2505496,-7.8912888,-2.0398598\n840209261489040,2.7868507,-9.433155,-0.92895025\n840209326498415,2.3463178,-8.504205,-1.1300632\n840209391509353,2.8443117,-10.01734,-1.6472107\n840209456485915,2.0015526,-9.040505,-1.685518\n840209521496853,1.5801731,-9.375694,-1.7046716\n840209586506228,0.45968673,-11.36767,-2.0398598\n840209651484353,1.1013328,-11.243171,0.38307226\n840209716493728,0.40222588,-9.414001,-2.920926\n840209781503103,-2.336741,-9.500193,-0.6895301\n840209846512478,-0.82360536,-10.965444,-3.9264908\n840209911490603,0.46926352,-9.80665,-2.2409728\n840209976501540,-1.8579005,-10.01734,-2.0685902\n840210041510915,-2.3080103,-9.528923,-1.8579005\n840210106489040,-3.1028855,-10.151415,-0.91937345\n840210171498415,-3.4284968,-10.151415,-2.3175871\n840210236509353,-1.436521,-8.791509,-2.6240451\n840210301485915,-7.833828,-7.1634517,-0.8810662\n840210366496853,-3.8594532,-7.4986396,-2.3463178\n840210431506228,-6.1004257,-9.040505,-0.12449849\n840210496484353,-5.142745,-9.873688,-2.432509\n840210561493728,-5.995081,-7.3645644,-2.681506\n840210626503103,-9.078813,-6.129156,-0.8619126\n840210691512478,-7.5561004,-7.8625584,1.7717092\n840210756490603,-8.37013,-5.841852,-1.091756\n840210821501540,-11.894394,-4.76925,4.060566\n840210886510915,-10.524911,-1.6472107,3.9552212\n840210951489040,-9.854534,-0.9959879,8.485051\n840211016498415,-6.8091097,-0.23942018,5.5737014\n840211081507790,-5.2863975,-1.1779473,7.747637\n840211146485915,-4.7788267,-0.4405331,7.2304893\n840211211495290,-4.6830587,-1.1683705,8.839393\n840211276504665,-4.405331,0.30645782,7.785944\n840211341484353,-3.0933087,1.3599066,7.699753\n840211406493728,-3.514688,1.8387469,6.8569937\n840211471503103,-2.5378537,2.72939,9.174581\n840211536512478,-1.9632454,2.633622,7.8912888\n840211601490603,-4.357447,4.146757,8.446744\n840211666501540,-2.528277,1.3790601,9.56723\n840211731509353,-2.5761611,5.4204726,-10.70687\n840211796489040,1.8100165,4.050989,11.21444\n840211861496853,0.11492168,2.566584,7.8050976\n840211926507790,0.8331822,3.5913026,10.601525\n840211991485915,-0.23942018,3.7445314,9.040505\n840212056495290,-0.20111294,4.185065,8.7053175\n840212121506228,0.36391866,3.6774938,9.509769\n";

    assertEquals(4, runStepCounterOnData(data));
  }
}