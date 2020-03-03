package io.invertase.firebase.admob;

import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.invertase.firebase.Utils;

class RNFirebaseAdMobUtils {

  /**
   * Convert common AdMob errors into a standard format
   *
   * @param errorCode
   * @return
   */
  static WritableMap errorCodeToMap(int errorCode) {
    WritableMap map = Arguments.createMap();

    switch (errorCode) {
      case PublisherAdRequest.ERROR_CODE_INTERNAL_ERROR:
        map.putString("code", "admob/error-code-internal-error");
        map.putString(
          "message",
          "Something happened internally; for instance, an invalid response was received from the ad server."
        );
        break;
      case PublisherAdRequest.ERROR_CODE_INVALID_REQUEST:
        map.putString("code", "admob/error-code-invalid-request");
        map.putString(
          "message",
          "The ad request was invalid; for instance, the ad unit ID was incorrect."
        );
        break;
      case PublisherAdRequest.ERROR_CODE_NETWORK_ERROR:
        map.putString("code", "admob/error-code-network-error");
        map.putString("message", "The ad request was unsuccessful due to network connectivity.");
        break;
      case PublisherAdRequest.ERROR_CODE_NO_FILL:
        map.putString("code", "admob/error-code-no-fill");
        map.putString(
          "message",
          "The ad request was successful, but no ad was returned due to lack of ad inventory."
        );
        break;
    }

    return map;
  }

  static PublisherAdRequest buildRequest(ReadableMap request) {
    PublisherAdRequest.Builder requestBuilder = new PublisherAdRequest.Builder();
    Bundle extras = new Bundle();

    if (request.hasKey("isDesignedForFamilies")) {
      requestBuilder.setIsDesignedForFamilies(request.getBoolean("isDesignedForFamilies"));
    }

    if (request.hasKey("tagForChildDirectedTreatment")) {
      requestBuilder.tagForChildDirectedTreatment(request.getBoolean("tagForChildDirectedTreatment"));
    }

    if (request.hasKey("contentUrl")) {
      requestBuilder.setContentUrl(request.getString("contentUrl"));
    }

    if (request.hasKey("requestAgent")) {
      requestBuilder.setRequestAgent(request.getString("requestAgent"));
    }

    if (request.hasKey("networkExtras")) {
      Map<String, Object> networkExtras = request.getMap("networkExtras").toHashMap();

      for (Map.Entry<String, Object> entry : networkExtras.entrySet()) {
        String key = entry.getKey();
        String value = (String) entry.getValue();
        extras.putString(key, value);
      }
    }

    requestBuilder.addNetworkExtrasBundle(AdMobAdapter.class, extras);

    if (request.hasKey("gender")) {
      String gender = request.getString("gender");
      switch (gender) {
        case "male":
          requestBuilder.setGender(PublisherAdRequest.GENDER_MALE);
          break;
        case "female":
          requestBuilder.setGender(PublisherAdRequest.GENDER_FEMALE);
          break;
        case "unknown":
          requestBuilder.setGender(PublisherAdRequest.GENDER_UNKNOWN);
          break;
      }
    }

    // Handle testDevices array
    ReadableArray testDevices = request.getArray("testDevices");
    List<Object> testDevicesList = Utils.recursivelyDeconstructReadableArray(testDevices);

    for (Object deviceId : testDevicesList) {
      if (deviceId == "DEVICE_ID_EMULATOR") {
        requestBuilder.addTestDevice(PublisherAdRequest.DEVICE_ID_EMULATOR);
      } else {
        requestBuilder.addTestDevice((String) deviceId);
      }
    }

    // Handle keywords array
    ReadableArray keywords = request.getArray("keywords");
    List<Object> keywordsList = Utils.recursivelyDeconstructReadableArray(keywords);

    for (Object word : keywordsList) {
      requestBuilder.addKeyword((String) word);
    }

    return requestBuilder.build();
  }

  static VideoOptions.Builder buildVideoOptions(ReadableMap options) {
    VideoOptions.Builder optionsBuilder = new VideoOptions.Builder();

    // Default true
    optionsBuilder.setStartMuted(options.getBoolean("startMuted"));

    return optionsBuilder;
  }

  /**
   * Mat the size prop to the AdSize[]
   *
   * @param value
   * @return
   */
  static ArrayList<AdSize> stringToAdSizeArray(String value) {
    String[] stringSplit = value.split(",");

    ArrayList<AdSize> result = new ArrayList<AdSize>();

    for (int i = 0; i < stringSplit.length; i++) {
      result.add(stringToAdSize(stringSplit[i]));
    }

    return result;
  }

  /**
   * Map the size prop to the AdSize
   *
   * @param value
   * @return
   */
  static AdSize stringToAdSize(String value) {
    Pattern pattern = Pattern.compile("([0-9]+)x([0-9]+)");
    Matcher matcher = pattern.matcher(value);

    // If size is "valXval"
    if (matcher.find()) {
      int width = Integer.parseInt(matcher.group(1));
      int height = Integer.parseInt(matcher.group(2));
      return new AdSize(width, height);
    }

    switch (value.toUpperCase()) {
      default:
      case "BANNER":
        return AdSize.BANNER;
      case "LARGE_BANNER":
        return AdSize.LARGE_BANNER;
      case "MEDIUM_RECTANGLE":
        return AdSize.MEDIUM_RECTANGLE;
      case "FULL_BANNER":
        return AdSize.FULL_BANNER;
      case "LEADERBOARD":
        return AdSize.LEADERBOARD;
      case "SMART_BANNER":
        return AdSize.SMART_BANNER;
      case "SMART_BANNER_LANDSCAPE":
        return AdSize.SMART_BANNER;
    }
  }
}
