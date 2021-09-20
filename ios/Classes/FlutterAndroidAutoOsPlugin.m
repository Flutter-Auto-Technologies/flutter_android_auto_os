#import "FlutterAndroidAutoOsPlugin.h"
#if __has_include(<flutter_android_auto_os/flutter_android_auto_os-Swift.h>)
#import <flutter_android_auto_os/flutter_android_auto_os-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_android_auto_os-Swift.h"
#endif

@implementation FlutterAndroidAutoOsPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterAndroidAutoOsPlugin registerWithRegistrar:registrar];
}
@end
