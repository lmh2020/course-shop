<template>
  <div class="icon-large icon-clock">
    <div class="clock">
      <ol>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
      </ol>
      <div id="div-hour" class="hour" :style="{transform: style.hour}"></div>
      <div id="div-minute" class="minute" :style="{transform: style.minute}"></div>
      <div id="div-second" class="second" :style="{transform: style.second}"></div>
    </div>
  </div>
</template>

<script>


export default {
  data() {
    return {
      timeInterval: null,
      style: {
        hour: null,
        minute: null,
        second: null,
      }
    };
  },
  props: {},
  created() {
    this.timeInterval = setInterval(() => {
      this.startClock()
    }, 1000)
  },

  destroyed() {
    if (this.timeInterval){
      window.clearInterval(this.timeInterval)
      this.timeInterval = null
    }
  },

  methods: {
    startClock() {
      // 当前时间
      var now = new Date(),
        // 午夜12点整
        midnight = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0),
        // 当前时间与午夜12的之间的毫秒差
        ms = now.getTime() - midnight.getTime(),
        // 计算时、分、秒
        hh = ms / (1000 * 60 * 60),
        mm = hh * 60,
        ss = mm * 60;
      // 实现时钟旋转
      this.style.hour = "rotate(" + (hh * 30 + (hh / 2)) + "deg)";
      this.style.minute = "rotate(" + (mm * 6) + "deg)";
      this.style.second = "rotate(" + (ss * 6) + "deg)";
    }
  }

}

</script>

<style lang="scss" scoped>

.icon-large {
  z-index: 999;
  width: 220px;
  height: 220px;
  border-radius: 38px;
  position: absolute;
  //margin-left: 60%;
  //top: 50%;
  //left: 50%;
  //transform: translate(-50%, -50%)
}

.icon-clock {
  overflow: hidden;
  background: #000
}

.clock {
  width: 192px;
  height: 192px;
  border-radius: 50%;
  background: #f1f1f1;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%)
}

.clock ol {
  list-style-type: none;
  width: 100%;
  height: 100%;
  position: relative;
  margin: 0;
  padding: 0
}

.clock ol li {
  counter-increment: labelCounter;
  position: absolute;
  font-size: 1.25em;
  color: black;
}

.clock ol li:before {
  font-family: Helvetica;
  content: counter(labelCounter) ""
}

.clock ol li:nth-child(1) {
  right: 55px;
  top: 20px
}

.clock ol li:nth-child(2) {
  right: 25px;
  top: 50px
}

.clock ol li:nth-child(3) {
  right: 12px;
  top: 85px
}

.clock ol li:nth-child(4) {
  right: 25px;
  top: 125px
}

.clock ol li:nth-child(5) {
  right: 55px;
  top: 150px
}

.clock ol li:nth-child(6) {
  right: 90px;
  top: 160px
}

.clock ol li:nth-child(7) {
  right: 125px;
  top: 150px
}

.clock ol li:nth-child(8) {
  right: 155px;
  top: 125px
}

.clock ol li:nth-child(9) {
  right: 165px;
  top: 85px
}

.clock ol li:nth-child(10) {
  right: 150px;
  top: 50px
}

.clock ol li:nth-child(11) {
  right: 120px;
  top: 20px
}

.clock ol li:nth-child(12) {
  right: 85px;
  top: 10px
}

.hour {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #303030;
  position: absolute;
  top: 50%;
  left: 50%;
  margin-top: -7px;
  margin-left: -7px
}

.hour:after, .hour:before {
  content: "";
  display: block;
  position: absolute
}

.hour:before {
  width: 8px;
  height: 65px;
  border-radius: 4px;
  background: #303030;
  position: absolute;
  bottom: 2px;
  left: 50%;
  transform: translate(-50%, 0)
}

.minute {
  width: 0;
  height: 0;
  border-radius: 50%;
  background: #303030;
  position: absolute;
  top: 50%;
  left: 50%
}

.minute:after, .minute:before {
  content: "";
  display: block;
  position: absolute
}

.minute:before {
  width: 6px;
  height: 85px;
  border-radius: 4px;
  background: #303030;
  position: absolute;
  bottom: 2px;
  left: 50%;
  transform: translate(-50%, 0)
}

.second {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #ff8000;
  border: 2px solid #ff8000;
  position: absolute;
  top: 50%;
  left: 50%;
  margin-top: -4px;
  margin-left: -4px
}

.second:after, .second:before {
  content: "";
  display: block;
  position: absolute
}

.second:before {
  width: 2px;
  height: 105px;
  border-radius: 4px;
  background: #ff8000;
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translate(-50%, 0)
}

</style>
