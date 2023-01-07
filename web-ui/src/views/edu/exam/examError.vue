<template>
  <div>
    <div class="wavetext">
      <div class="wavetop">
        <div class="wavets" v-text="$route.query.msg"></div>
      </div>
      <div class="waveqcz">
      </div>
      <div class="wavegohome waveinput">
        <router-link to="/index">去首页</router-link>
      </div>
      <div class="waveclose waveinput">
        <router-link to="/education/examList">回到我的考试页面</router-link>
      </div>
      <div class="waveqcz">
      </div>
      <div class="waveqczfd">
      </div>
    </div>
    <div class="waveWrapper waveAnimation">
      <div class="waveWrapperInner bgTop" :style="{backgroundImage: `url(${examCloseBg})`}">
        <div class="wave waveTop" :style="{backgroundImage: `url(${waveTop})`}">
        </div>
      </div>
      <div class="waveWrapperInner bgMiddle">
        <div class="wave waveMiddle" :style="{backgroundImage: `url(${waveMid})`}">
        </div>
      </div>
      <div class="waveWrapperInner bgBottom">
        <div class="wave waveBottom" :style="{backgroundImage: `url(${waveBot})`}">
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import examCloseBg from '@/assets/image/exam-close-bg.svg'
import waveBot from '@/assets/image/wave-bot.png'
import waveMid from '@/assets/image/wave-mid.png'
import waveTop from '@/assets/image/wave-top.png'

export default {
  name: 'examClose',
  data() {
    return {
      errorMsg: this.$route.query.msg,
      examCloseBg: examCloseBg,
      waveBot: waveBot,
      waveMid: waveMid,
      waveTop: waveTop,
    }
  },
  created() {
    console.log(this.$route)
  },
  methods: {}
}
</script>

<style lang="scss" scoped>
@keyframes move_wave {
  0% {
    transform: translateX(0) translateZ(0) scaleY(1)
  }
  50% {
    transform: translateX(-25%) translateZ(0) scaleY(0.55)
  }
  100% {
    transform: translateX(-50%) translateZ(0) scaleY(1)
  }
}

.waveWrapper {
  overflow: hidden;
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  top: 0;
  margin: auto;
}

.waveWrapperInner {
  position: absolute;
  width: 100%;
  overflow: hidden;
  height: 100%;
  bottom: -1px;
  //background-image: url('@/assets/image/exam-close-bg.svg');
  background-repeat: no-repeat;
  background-size: 100%;
}

.bgTop {
  z-index: 15;
  opacity: 0.5;
}

.bgMiddle {
  z-index: 10;
  opacity: 0.75;
}

.bgBottom {
  z-index: 5;
}

.wave {
  position: absolute;
  left: 0;
  width: 200%;
  height: 100%;
  background-repeat: repeat no-repeat;
  background-position: 0 bottom;
  transform-origin: center bottom;
}

.waveTop {
  background-size: 50% 150px;
}

.waveAnimation .waveTop {
  animation: move-wave 3s;
  -webkit-animation: move-wave 3s;
  -webkit-animation-delay: 1s;
  animation-delay: 1s;
}

.waveMiddle {
  background-size: 50% 190px;
}

.waveAnimation .waveMiddle {
  animation: move_wave 10s linear infinite;
}

.waveBottom {
  background-size: 50% 200px;
}

.waveAnimation .waveBottom {
  animation: move_wave 15s linear infinite;
}

.wavetext {
  position: absolute;
  z-index: 99999999;
  width: 40%;
  right: 10%;
  top: 25%;
}

.wavetop {
  margin-bottom: 3%;
}

.wavelx {
  width: 32%;
  float: left;
}

.wavets {
  float: left;
  font-size: 200%;
  //width: 62%;
  padding-top: 10%;
  padding-left: 5%;
  padding-bottom: 2%;
}

.wavecz {
  width: 100%;
  font-size: 150%;
  padding-left: 5%;
}

.waveqcz {
  clear: left;
}

.wavegohome {
  margin-top: 5%;
  float: left;
  margin-left: 7%;
}

.waveclose {
  margin-top: 5%;
  float: left;
  margin-left: 7%;
}

.wavegohome a, .waveclose a {
  margin: 2px;
  color: #ffb957;
  text-decoration: none;
  font-size: 170%;
}

.waveinput {
  border: 2px solid #ffb957;
  padding: 0.2em 1em 0.3em 1em;
  border-radius: 8em;
}
</style>
