const angleValueEl = document.getElementById('angleValue');
const statusEl = document.getElementById('status');
const enableBtn = document.getElementById('enableButton');

let listening = false;

function setBackground(isOk) {
  document.body.style.backgroundColor = isOk ? '#17c964' : '#f31260';
  const themeMeta = document.querySelector('meta[name="theme-color"]');
  if (themeMeta) themeMeta.setAttribute('content', isOk ? '#17c964' : '#f31260');
}

function updateAngleDisplay(degrees) {
  angleValueEl.textContent = Number.isFinite(degrees) ? `${degrees.toFixed(1)}°` : '--.-°';
}

function withinUprightRange(betaDeg) {
  const abs = Math.abs(betaDeg);
  return abs >= 80 && abs <= 90;
}

function handleOrientation(event) {
  listening = true;
  const beta = event && typeof event.beta === 'number' ? event.beta : null;
  if (beta == null) {
    statusEl.textContent = 'No angle data';
    setBackground(false);
    updateAngleDisplay(NaN);
    return;
  }

  updateAngleDisplay(beta);
  const ok = withinUprightRange(beta);
  setBackground(ok);
  statusEl.textContent = ok ? 'Upright (80–90°)' : 'Adjust phone tilt';
}

function startListening() {
  if (listening) return;
  window.addEventListener('deviceorientation', handleOrientation, true);
  statusEl.textContent = 'Reading angle…';
}

async function requestPermissionIfNeeded() {
  const needsMotionPermission = typeof DeviceMotionEvent !== 'undefined' && typeof DeviceMotionEvent.requestPermission === 'function';
  const needsOrientationPermission = typeof DeviceOrientationEvent !== 'undefined' && typeof DeviceOrientationEvent.requestPermission === 'function';

  if (needsMotionPermission || needsOrientationPermission) {
    enableBtn.hidden = false;
    enableBtn.addEventListener('click', async () => {
      try {
        let motionGranted = 'granted';
        if (needsMotionPermission) {
          motionGranted = await DeviceMotionEvent.requestPermission();
        }
        let orientationGranted = 'granted';
        if (needsOrientationPermission) {
          orientationGranted = await DeviceOrientationEvent.requestPermission();
        }
        if (motionGranted === 'granted' && orientationGranted === 'granted') {
          enableBtn.hidden = true;
          startListening();
        } else {
          statusEl.textContent = 'Permission denied. Tap the button to try again.';
        }
      } catch (err) {
        statusEl.textContent = 'Permission error. Tap to retry.';
        console.error(err);
      }
    }, { once: false });
  } else {
    startListening();
  }
}

function init() {
  setBackground(false);
  requestPermissionIfNeeded();

  setTimeout(() => {
    if (!listening) {
      statusEl.textContent = 'No motion sensors detected. Try on a mobile device.';
    }
  }, 3000);
}

document.addEventListener('DOMContentLoaded', init);

