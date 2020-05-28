import React, { useRef, useEffect, useState } from 'react';
import AdMobComponent from './AdMobComponent';
import { Platform } from 'react-native';

let globalUniqueId = 0;

function Banner({ ...props
}) {
  const [uniqueId, setUniqueId] = useState(globalUniqueId++);

  const ref = useRef();
  useEffect(() => {
    return () => {
      setUniqueId(globalUniqueId++);
      if (Platform.OS == 'ios') {
        ref.current.destroy();
      }
    };
  }, []);

  return <AdMobComponent key={uniqueId} ref={ref} {...props} class="RNFirebaseAdMobBanner" />;
}

Banner.propTypes = AdMobComponent.propTypes;
Banner.defaultProps = {
  size: 'SMART_BANNER'
};
export default Banner;
