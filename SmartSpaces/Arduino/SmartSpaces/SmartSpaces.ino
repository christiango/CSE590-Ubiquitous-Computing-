#include "ble_config.h"

/*
 * Provides skeleton code to interact with the Android FaceTrackerBLE app 
 * 
 * Created by Jon Froehlich, May 7, 2018
 * 
 * Based on previous code by Liang He, Bjoern Hartmann, 
 * Chris Dziemborowicz and the RedBear Team. See: 
 * https://github.com/jonfroehlich/CSE590Sp2018/tree/master/A03-BLEAdvanced
 */

#if defined(ARDUINO) 
SYSTEM_MODE(SEMI_AUTOMATIC); 
#endif

#define RECEIVE_MAX_LEN  5 // TODO: change this based on how much data you are sending from Android 
#define SEND_MAX_LEN    3

// Must be an integer between 1 and 9 and and must also be set to len(BLE_SHORT_NAME) + 1
#define BLE_SHORT_NAME_LEN 8 

#define BLE_SHORT_NAME 'c','h','r','i','s','g','o'  

#define SERVO_PIN D0
#define MAX_SERVO_ANGLE  180
#define MIN_SERVO_ANGLE  0

#define TRIG_PIN D1
#define ECHO_PIN D2
#define BLE_DEVICE_CONNECTED_DIGITAL_OUT_PIN D7
#define LED_PIN D8
#define BUZZER_PIN D9

#define LED_COUNT2_PIN D13 // the high order bit of the led face count
#define LED_COUNT1_PIN D12 // the middle order bit of the led face count
#define LED_COUNT0_PIN D11 // the low order bit of the led face count

// Anything over 400 cm (23200 us pulse) is "out of range"
const unsigned int MAX_DIST = 23200;

const unsigned int ALARM_DIST_CM = 50;

Servo myservo; 

// Device connected and disconnected callbacks
void deviceConnectedCallback(BLEStatus_t status, uint16_t handle);
void deviceDisconnectedCallback(uint16_t handle);

// UUID is used to find the device by other BLE-abled devices
static uint8_t service1_uuid[16]    = { 0x71,0x3d,0x00,0x00,0x50,0x3e,0x4c,0x75,0xba,0x94,0x31,0x48,0xf1,0x8d,0x94,0x1e };
static uint8_t service1_tx_uuid[16] = { 0x71,0x3d,0x00,0x03,0x50,0x3e,0x4c,0x75,0xba,0x94,0x31,0x48,0xf1,0x8d,0x94,0x1e };
static uint8_t service1_rx_uuid[16] = { 0x71,0x3d,0x00,0x02,0x50,0x3e,0x4c,0x75,0xba,0x94,0x31,0x48,0xf1,0x8d,0x94,0x1e };

// Define the receive and send handlers
static uint16_t receive_handle = 0x0000; // recieve
static uint16_t send_handle = 0x0000; // send

static uint8_t receive_data[RECEIVE_MAX_LEN] = { 0x01 };
int bleReceiveDataCallback(uint16_t value_handle, uint8_t *buffer, uint16_t size); // function declaration for receiving data callback
static uint8_t send_data[SEND_MAX_LEN] = { 0x00 };

// Define the configuration data
static uint8_t adv_data[] = {
  0x02,
  BLE_GAP_AD_TYPE_FLAGS,
  BLE_GAP_ADV_FLAGS_LE_ONLY_GENERAL_DISC_MODE, 
  
  BLE_SHORT_NAME_LEN,
  BLE_GAP_AD_TYPE_SHORT_LOCAL_NAME,
  BLE_SHORT_NAME, 
  
  0x11,
  BLE_GAP_AD_TYPE_128BIT_SERVICE_UUID_COMPLETE,
  0x1e,0x94,0x8d,0xf1,0x48,0x31,0x94,0xba,0x75,0x4c,0x3e,0x50,0x00,0x00,0x3d,0x71 
};

static btstack_timer_source_t send_characteristic;
static void bleSendDataTimerCallback(btstack_timer_source_t *ts); // function declaration for sending data callback
int _sendDataFrequency = 200; // 200ms (how often to read the pins and transmit the data to Android)

// Distance of ultrasonic reading in CM
float _distance;

void setup() {
  Serial.begin(115200);
  delay(5000);
  Serial.println("Face Tracker BLE Demo.");

  // Initialize ble_stack.
  ble.init();
  
  // Register BLE callback functions
  ble.onConnectedCallback(bleConnectedCallback);
  ble.onDisconnectedCallback(bleDisconnectedCallback);

  //lots of standard initialization hidden in here - see ble_config.cpp
  configureBLE(); 
  
  // Set BLE advertising data
  ble.setAdvertisementData(sizeof(adv_data), adv_data);
  
  // Register BLE callback functions
  ble.onDataWriteCallback(bleReceiveDataCallback);

  // Add user defined service and characteristics
  ble.addService(service1_uuid);
  receive_handle = ble.addCharacteristicDynamic(service1_tx_uuid, ATT_PROPERTY_NOTIFY|ATT_PROPERTY_WRITE|ATT_PROPERTY_WRITE_WITHOUT_RESPONSE, receive_data, RECEIVE_MAX_LEN);
  send_handle = ble.addCharacteristicDynamic(service1_rx_uuid, ATT_PROPERTY_NOTIFY, send_data, SEND_MAX_LEN);

  // BLE peripheral starts advertising now.
  ble.startAdvertising();
  Serial.println("BLE start advertising.");

  // Setup pins
  myservo.attach(D0);
  myservo.write( (int)((MAX_SERVO_ANGLE - MIN_SERVO_ANGLE) / 2.0) );
  pinMode(TRIG_PIN, OUTPUT);
  digitalWrite(TRIG_PIN, LOW);
  pinMode(LED_PIN, OUTPUT);
  pinMode(BUZZER_PIN, OUTPUT);

  pinMode(LED_COUNT2_PIN, OUTPUT);
  pinMode(LED_COUNT1_PIN, OUTPUT);
  pinMode(LED_COUNT0_PIN, OUTPUT);

  // Start a task to check status of the pins on your RedBear Duo
  // Works by polling every X milliseconds where X is _sendDataFrequency
  send_characteristic.process = &bleSendDataTimerCallback;
  ble.setTimer(&send_characteristic, _sendDataFrequency); 
  ble.addTimer(&send_characteristic);
}

void loop() 
{
  unsigned long t1;
  unsigned long t2;
  unsigned long pulse_width;
  
  // Hold the trigger pin high for at least 10 us
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);

  // Wait for pulse on echo pin
  while ( digitalRead(ECHO_PIN) == 0 );

  // Measure how long the echo pin was held high (pulse width)
  // Note: the micros() counter will overflow after ~70 min
  // TODO: We probably need to check for a timeout here just in case
  // the ECHO_PIN never goes HIGH... so like
  // while ( digitalRead(ECHO_PIN) == 1 && micros() - t1 < threshold);
  t1 = micros();
  while ( digitalRead(ECHO_PIN) == 1);
  t2 = micros();
  pulse_width = t2 - t1;

  // Calculate distance in centimeters and inches. The constants
  // are found in the datasheet, and calculated from the assumed speed 
  // of sound in air at sea level (~340 m/s).
  // Datasheet: https://cdn.sparkfun.com/datasheets/Sensors/Proximity/HCSR04.pdf
  _distance = pulse_width / 58.0;

  if (_distance <= ALARM_DIST_CM) {
     digitalWrite(LED_PIN, HIGH);
     tone(BUZZER_PIN, 6000);
     delay(60);
     digitalWrite(LED_PIN, LOW);
     noTone(BUZZER_PIN);
     delay(60);
  } else {
    delay(60);
  }
}

/**
 * @brief Connect handle.
 *
 * @param[in]  status   BLE_STATUS_CONNECTION_ERROR or BLE_STATUS_OK.
 * @param[in]  handle   Connect handle.
 *
 * @retval None
 */
void bleConnectedCallback(BLEStatus_t status, uint16_t handle) {
  switch (status) {
    case BLE_STATUS_OK:
      Serial.println("BLE device connected!");
      digitalWrite(BLE_DEVICE_CONNECTED_DIGITAL_OUT_PIN, HIGH);
      break;
    default: break;
  }
}

/**
 * @brief Disconnect handle.
 *
 * @param[in]  handle   Connect handle.
 *
 * @retval None
 */
void bleDisconnectedCallback(uint16_t handle) {
  Serial.println("BLE device disconnected.");
  digitalWrite(BLE_DEVICE_CONNECTED_DIGITAL_OUT_PIN, LOW);
}

/**
 * @brief Callback for receiving data from Android (or whatever device you're connected to).
 *
 * @param[in]  value_handle  
 * @param[in]  *buffer       The buffer pointer of writting data.
 * @param[in]  size          The length of writting data.   
 *
 * @retval 
 */
int bleReceiveDataCallback(uint16_t value_handle, uint8_t *buffer, uint16_t size) {

  if (receive_handle == value_handle) {
    memcpy(receive_data, buffer, RECEIVE_MAX_LEN);
    Serial.print("Received data: ");
    for (uint8_t index = 0; index < RECEIVE_MAX_LEN; index++) {
      Serial.print(receive_data[index]);
      Serial.print(" ");
    }
    Serial.println(" ");
    
    // process the data. 
    if (receive_data[0] == 0x01) { //receive the face data 
      myservo.write(receive_data[1]);
    } else if (receive_data[0] == 0x02) { // receive the number of faces
      updateFaceCount(receive_data[1]);
    }
  }
  return 0;
}

void updateFaceCount(byte count) {
  digitalWrite(LED_COUNT2_PIN, count & 4);
  digitalWrite(LED_COUNT1_PIN, count & 2);
  digitalWrite(LED_COUNT0_PIN, count & 1);
}

/**
 * @brief Timer task for sending status change to client.
 * @param[in]  *ts   
 * @retval None
 * 
 * Send the data from either analog read or digital read back to 
 * the connected BLE device (e.g., Android)
 */
static void bleSendDataTimerCallback(btstack_timer_source_t *ts) {
  send_data[0] = _distance;
  ble.sendNotify(send_handle, send_data, SEND_MAX_LEN);

  // Restart timer.
  ble.setTimer(ts, 200);
  ble.addTimer(ts);
}
