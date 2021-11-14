import React, { useState, useRef, useEffect } from "react";

export default function withClickOutsideFollowerDialog(WrappedComponent) {
  const Component = (props) => {
    const [openFollowerDialog, setOpenFollowerDialog] = useState(false);

    const ref = useRef();

    useEffect(() => {
      const handleClickOutside = (event) => {
        if (!ref.current.contains(event.target)) {
          setOpenFollowerDialog(false);
        }
      };
      if(ref){
          document.addEventListener("mousedown", handleClickOutside);
      }
      else {
          document.removeEventListener("mousedown", handleClickOutside)
      }
      return () => document.removeEventListener("mousedown", handleClickOutside)
      
    }, [ref]);


    return <WrappedComponent openFollowerDialog={openFollowerDialog} setOpenFollowerDialog={setOpenFollowerDialog} ref={ref} />;
  };

  return Component;
}