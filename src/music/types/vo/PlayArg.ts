export interface PlayArg {
    currentMusicId?: number | null,
    playMode: 'order' | 'random' | 'loop',
    volume: number,
    playDuration: number
}
