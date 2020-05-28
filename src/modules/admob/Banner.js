import React, { useRef, useEffect } from 'react';
import AdMobComponent from './AdMobComponent';
import { Platform } from 'react-native';

function Banner({ ...props
}) {
  const ref = useRef()
  useEffect(()=>{
    return ()=> {
      if (Platform.OS == 'ios') {
        ref.current.destroy()
      }
    }
  })
  return <AdMobComponent ref={ref} {...props} class="RNFirebaseAdMobBanner" />;
}

Banner.propTypes = AdMobComponent.propTypes;

Banner.defaultProps = {
  size: 'SMART_BANNER',
};

export default Banner;
