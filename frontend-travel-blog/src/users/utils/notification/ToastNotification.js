import { toast } from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';

export const successNotification = (message) => {
    return toast.success(message , {
        position: "bottom-left",
          autoClose: 3000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
    })
}

export const errorNotification = (message) => {
    toast.error(message, {
        position: "bottom-left",
        autoClose: 3000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
      });
}

export const warnNotification = (message) => {
    toast.warn(message, {
        position: "bottom-left",
        autoClose: 3000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
    });
}