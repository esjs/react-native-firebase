import React, { useRef, useEffect } from 'react';
import AdMobComponent from './AdMobComponent';

function Banner({ ...props
}) {
  const ref = useRef()
  useEffect(()=>{
    return ()=> {
      ref.current.destroy()
    }
  })
  return <AdMobComponent ref={ref} {...props} class="RNFirebaseAdMobBanner" />;
}

Banner.propTypes = AdMobComponent.propTypes;

Banner.defaultProps = {
  size: 'SMART_BANNER',
};

export default Banner;
