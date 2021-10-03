package com.example.flutterandroidautoos.flutter_android_auto_os;

import android.car.Car;
import android.car.VehiclePropertyIds;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FlutterAndroidAutoOsPlugin */
public class FlutterAndroidAutoOsPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Car car;
  private CarPropertyManager carPropertyManager;
  private EventChannel carPropertyManagerChannel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    final Context context = flutterPluginBinding.getApplicationContext();
    setupChannels(context, flutterPluginBinding.getBinaryMessenger());

  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    teardownEventChannels();
  }

  private void setupChannels(Context context, BinaryMessenger messenger) {
    car=Car.createCar(context);
    carPropertyManager= (CarPropertyManager) car.getCarManager(Car.PROPERTY_SERVICE);
    carPropertyManagerChannel=new EventChannel(messenger, "car_gear");
    carPropertyManagerChannel.setStreamHandler(new EventChannel.StreamHandler() {
      @Override
      public void onListen(Object arguments, EventChannel.EventSink events) {
        carPropertyManager.registerCallback(new CarPropertyManager.CarPropertyEventCallback(){

          @Override
          public void onChangeEvent(CarPropertyValue carPropertyValue) {
            Log.d("CarProperty Value:", String.valueOf(carPropertyValue));
          }

          @Override
          public void onErrorEvent(int i, int i1) {
            Log.d("Error:", "Received error car property event, propId="+i);
          }
        }, VehiclePropertyIds.CURRENT_GEAR,CarPropertyManager.SENSOR_RATE_ONCHANGE);
        Log.d("listen:", (String) arguments);
      }

      @Override
      public void onCancel(Object arguments) {
//        carPropertyManager.unregisterCallback();
        Log.d("Stop:", (String) arguments);
      }
    });
    channel = new MethodChannel(messenger, "flutter_android_auto_os");
    channel.setMethodCallHandler(this);
  }

  private void teardownEventChannels() {
    channel.setMethodCallHandler(null);
    carPropertyManagerChannel.setStreamHandler(null);
  }
}
