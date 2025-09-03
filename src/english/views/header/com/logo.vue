<script setup lang="ts">
import { onMounted } from 'vue'
import { animate } from "animejs";

const Animation = () => {
  const rotateAnimation = animate( '.logo-box',{
    rotate: 360,
    autoplay: false,
    loop: true,
    ease: 'linear',
  })

  return {
    rotateAnimation
  }
}


onMounted(() => {
  const animation = Animation()
  const element = document.querySelector('.logo-box')
  if (element === null) {
    return
  }
  element.addEventListener('mouseover', () => {
    animation.rotateAnimation.play()
  })
  element.addEventListener('mouseout', () => {
    animation.rotateAnimation.pause()
  })
});

</script>

<template>
  <div class="logo-box"></div>
</template>

<style scoped>
.logo-box {
  width: 75px;
  height: 75px;
  background-image: url('@/english/public/logo.png');
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  margin-left: 15px;
  margin-right: 5px;
  z-index: 1;
}
</style>
