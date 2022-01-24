package com.example.flutterandroidautoos.flutter_android_auto_os;

import android.app.Activity;
import android.car.Car;
import android.car.VehiclePropertyIds;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.CarSensorManager;
import android.car.hardware.property.CarPropertyManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener;

/** FlutterAndroidAutoOsPlugin */
public class FlutterAndroidAutoOsPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler , RequestPermissionsResultListener{
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Car car;
  private CarPropertyManager carPropertyManager;
  private EventChannel carPropertyManagerChannel;
  private FlutterPluginBinding flutterPluginBinding;
  private ActivityPluginBinding activityPluginBinding;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    this.flutterPluginBinding = flutterPluginBinding;
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
    this.flutterPluginBinding = null;
  }

  private void setupChannels(Context context, BinaryMessenger messenger) {
//    ActivityCompat.requestPermissions(activityPluginBinding.getActivity(),new String[] {
//            Car.PERMISSION_POWERTRAIN
//    },0);

    ActivityCompat.requestPermissions(activityPluginBinding.getActivity(),new String[] {
            Car.PERMISSION_SPEED
    },0);

    car=Car.createCar(context);
    carPropertyManager= (CarPropertyManager) car.getCarManager(Car.PROPERTY_SERVICE);
    carPropertyManagerChannel=new EventChannel(messenger, "car_gear");
    carPropertyManagerChannel.setStreamHandler(new EventChannel.StreamHandler() {
      @Override
      public void onListen(Object arguments, EventChannel.EventSink events) {
        carPropertyManager.registerCallback(new CarPropertyManager.CarPropertyEventCallback(){
          @Override
          public void onChangeEvent(CarPropertyValue carPropertyValue) {
            Log.d("MainActivity:", String.valueOf(carPropertyValue.getValue()));
            Log.d("MainActivity:", String.valueOf(carPropertyValue.describeContents()));
            Log.d("MainActivity:", String.valueOf(carPropertyValue.toString()));
            events.success(carPropertyValue.getValue());
          }

          @Override
          public void onErrorEvent(int i, int i1) {
            Log.d("MainActivity:", "Received error car property event, propId="+i);
          }
        }, VehiclePropertyIds.PERF_VEHICLE_SPEED,CarPropertyManager.SENSOR_RATE_NORMAL);
        Log.d("MainActivity:", "listening");
      }
      @Override
      public void onCancel(Object arguments) {
        Log.d("MainActivity:", "cancel");
      }
    });

    channel = new MethodChannel(messenger, "flutter_android_auto_os");
    channel.setMethodCallHandler(this);
  }

  private void teardownEventChannels() {
    channel.setMethodCallHandler(null);
    carPropertyManagerChannel.setStreamHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    this.activityPluginBinding = binding;
    setupChannels(flutterPluginBinding.getApplicationContext(), flutterPluginBinding.getBinaryMessenger());
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivity() {
    teardownEventChannels();
  }

  @Override
  public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (permissions[0] == Car.PERMISSION_SPEED && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      return true;
    }
    return false;
  }
}
