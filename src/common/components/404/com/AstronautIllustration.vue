<template>
  <div class="illustration">
    <div class="astronaut">
      <div class="helmet"></div>
      <div class="face"></div>
      <div class="body"></div>
      <div class="arm left"></div>
      <div class="arm right"></div>
      <div class="leg left"></div>
      <div class="leg right"></div>
    </div>
  </div>
</template>

<style scoped>
.illustration {
  position: relative;
  width: 300px;
  height: 300px;
  margin-left: 5rem;
  z-index: 2;
}

.astronaut {
  position: relative;
  width: 150px;
  height: 180px;
  margin: 0 auto;
  animation: float 6s ease-in-out infinite;
}

.helmet {
  position: absolute;
  width: 120px;
  height: 120px;
  background: #fff;
  border-radius: 50%;
  top: 0;
  left: 15px;
}

.face {
  position: absolute;
  width: 80px;
  height: 40px;
  background: #16213e;
  border-radius: 20px;
  top: 50px;
  left: 35px;
}

.body {
  position: absolute;
  width: 80px;
  height: 100px;
  background: #fff;
  border-radius: 40px;
  top: 100px;
  left: 35px;
}

.arm, .leg {
  position: absolute;
  background: #fff;
}

.arm {
  width: 25px;
  height: 60px;
  border-radius: 12.5px;
}

.arm.left {
  top: 110px;
  left: 10px;
  transform: rotate(20deg);
  animation: swing-left 3s ease-in-out infinite;
}

.arm.right {
  top: 110px;
  right: 10px;
  transform: rotate(-20deg);
  animation: swing-right 3s ease-in-out infinite;
}

.leg {
  width: 30px;
  height: 50px;
  border-radius: 15px;
  top: 190px;
}

.leg.left {
  left: 40px;
  animation: swing-left 3.5s ease-in-out infinite;
}

.leg.right {
  right: 40px;
  animation: swing-right 3.5s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@keyframes swing-left {
  0%, 100% { transform: rotate(20deg); }
  50% { transform: rotate(10deg); }
}

@keyframes swing-right {
  0%, 100% { transform: rotate(-20deg); }
  50% { transform: rotate(-10deg); }
}
</style>
