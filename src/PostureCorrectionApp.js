import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { 
  CheckCircle, 
  Clock, 
  Target, 
  TrendingUp, 
  Calendar, 
  Play, 
  Pause, 
  RotateCcw, 
  ChevronDown, 
  ChevronUp,
  AlertTriangle,
  Lightbulb,
  Heart,
  Zap
} from 'lucide-react';

const PostureCorrectionApp = () => {
  const STORAGE = useMemo(
    () => ({
      ACTIVE_TAB: 'pca_activeTab_v1',
      CURRENT_WEEK: 'pca_currentWeek_v1',
      COMPLETED_PREFIX: 'pca_completedExercises_',
    }),
    []
  );

  const todayKey = useMemo(() => new Date().toISOString().slice(0, 10), []);

  const safeLocalStorageGet = useCallback((key) => {
    try {
      return localStorage.getItem(key);
    } catch {
      return null;
    }
  }, []);

  const safeLocalStorageSet = useCallback((key, value) => {
    try {
      localStorage.setItem(key, value);
    } catch {
      // Ignore persistence failures (private mode, quota, etc.)
    }
  }, []);

  // State management for all app functionality
  const [activeTab, setActiveTab] = useState(() => {
    if (typeof window === 'undefined') return 'assessment';
    const raw = safeLocalStorageGet(STORAGE.ACTIVE_TAB);
    const allowed = new Set(['assessment', 'exercises', 'progress']);
    return raw && allowed.has(raw) ? raw : 'assessment';
  });

  const [currentWeek, setCurrentWeek] = useState(() => {
    if (typeof window === 'undefined') return 1;
    const raw = safeLocalStorageGet(STORAGE.CURRENT_WEEK);
    const parsed = raw ? Number(raw) : 1;
    if (!Number.isFinite(parsed)) return 1;
    return Math.min(12, Math.max(1, parsed));
  });

  const [completedExercises, setCompletedExercises] = useState(() => {
    if (typeof window === 'undefined') return {};
    const raw = safeLocalStorageGet(`${STORAGE.COMPLETED_PREFIX}${todayKey}`);
    if (!raw) return {};
    try {
      const parsed = JSON.parse(raw);
      return parsed && typeof parsed === 'object' ? parsed : {};
    } catch {
      return {};
    }
  });

  const [timerActive, setTimerActive] = useState(false);
  const [timeRemaining, setTimeRemaining] = useState(0);
  const [activeTimer, setActiveTimer] = useState(null);
  const [expandedExercise, setExpandedExercise] = useState(null);

  // Persist lightweight user state so the app feels "complete"
  useEffect(() => {
    safeLocalStorageSet(STORAGE.ACTIVE_TAB, activeTab);
  }, [activeTab, STORAGE, safeLocalStorageSet]);

  useEffect(() => {
    safeLocalStorageSet(STORAGE.CURRENT_WEEK, String(currentWeek));
  }, [currentWeek, STORAGE, safeLocalStorageSet]);

  useEffect(() => {
    safeLocalStorageSet(
      `${STORAGE.COMPLETED_PREFIX}${todayKey}`,
      JSON.stringify(completedExercises)
    );
  }, [completedExercises, todayKey, STORAGE, safeLocalStorageSet]);

  // Timer effect for countdown functionality
  useEffect(() => {
    let interval = null;
    if (timerActive && timeRemaining > 0) {
      interval = setInterval(() => {
        setTimeRemaining(time => time - 1);
      }, 1000);
    } else if (timeRemaining === 0 && timerActive) {
      setTimerActive(false);
      setActiveTimer(null);
    }
    return () => clearInterval(interval);
  }, [timerActive, timeRemaining]);

  // Timer control functions
  const startTimer = (exerciseId, duration) => {
    setActiveTimer(exerciseId);
    setTimeRemaining(duration);
    setTimerActive(true);
  };

  const pauseTimer = () => {
    setTimerActive((prev) => !prev);
  };

  const resetTimer = () => {
    setTimerActive(false);
    setTimeRemaining(0);
    setActiveTimer(null);
  };

  // Exercise completion tracking
  const markComplete = (exerciseId) => {
    setCompletedExercises(prev => ({
      ...prev,
      [exerciseId]: !prev[exerciseId]
    }));
  };

  // Time formatting utility
  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const imageBaseUrl = `${process.env.PUBLIC_URL}/exercises/`;

  // Comprehensive exercise database
  const exercises = useMemo(() => ({
    mobility: [
      {
        id: 'pec-stretch',
        name: 'Pec Stretch (Doorway)',
        duration: 45,
        reps: '3 sets x 45-60 sec',
        description: 'Stand in a doorway, forearms on the frame, step through until a stretch is felt in the chest.',
        tips: 'Keep your core engaged and avoid arching your back excessively.',
        frequency: 'Daily',
        icon: <Heart className="text-blue-600" size={20} />
      },
      {
        id: 'wall-angel',
        name: 'Wall Angel Pec Opener',
        duration: 30,
        reps: '2 sets x 10 reps',
        description: 'Back against wall, arms in goalpost position. Slowly raise and lower arms, keeping wrists/elbows against wall.',
        tips: 'Focus on keeping contact with the wall throughout the movement.',
        frequency: 'Daily',
        icon: <Target className="text-blue-600" size={20} />
      },
      {
        id: 'thoracic-extension',
        name: 'Thoracic Extension on Foam Roller',
        duration: 60,
        reps: '8-10 slow extensions',
        description: 'Place roller under mid-back, hands behind head, extend backward over roller.',
        tips: 'Move slowly and breathe deeply. Stop if you feel any sharp pain.',
        frequency: 'Daily',
        icon: <Zap className="text-blue-600" size={20} />
      }
    ],
    strengthening: [
      {
        id: 'scapular-retractions',
        name: 'Scapular Retractions (Band Rows)',
        duration: 45,
        reps: '3 sets x 12',
        description: 'Pull resistance band toward chest, squeeze shoulder blades together.',
        tips: 'Focus on squeezing shoulder blades, not just pulling with arms.',
        frequency: '3-4x/week',
        icon: <TrendingUp className="text-green-600" size={20} />
      },
      {
        id: 'prone-ytws',
        name: 'Prone YTWs',
        duration: 60,
        reps: '2-3 sets x 8-10 each',
        description: 'Lie face down, lift arms in a Y, then T, then W position. Focus on scapular squeeze.',
        tips: 'Quality over quantity - focus on form rather than arm height.',
        frequency: '3-4x/week',
        icon: <Target className="text-green-600" size={20} />
      },
      {
        id: 'face-pulls',
        name: 'Face Pulls (Band or Cable)',
        duration: 45,
        reps: '3 sets x 12',
        description: 'Pull toward nose/forehead, elbows high, external rotation emphasized.',
        tips: 'Think about pulling your shoulder blades back and down.',
        frequency: '3-4x/week',
        icon: <Zap className="text-green-600" size={20} />
      },
      {
        id: 'wall-slides',
        name: 'Reverse Wall Slides',
        duration: 30,
        reps: '2 sets x 10',
        description: 'Back flat to wall, wrists/elbows pressing back. Slide arms up/down without arching back.',
        tips: 'Maintain wall contact throughout the entire range of motion.',
        frequency: '3-4x/week',
        icon: <Heart className="text-green-600" size={20} />
      }
    ],
    alignment: [
      {
        id: 'chin-tucks',
        name: 'Chin Tucks (Deep Neck Flexor Activation)',
        duration: 50,
        reps: '10 reps x 5-8 sec holds',
        description: 'Lie flat, tuck chin straight back (as if making a double chin). Hold 5–8 sec.',
        tips: 'Imagine lengthening the back of your neck.',
        frequency: 'Daily',
        icon: <Target className="text-purple-600" size={20} />
      },
      {
        id: 'wall-posture',
        name: 'Wall Posture Drill',
        duration: 60,
        reps: '2-3 sets x 30-60 sec',
        description: 'Stand with back, shoulders, and head against wall. Maintain position.',
        tips: 'This is your target posture - memorize how it feels.',
        frequency: 'Daily',
        icon: <TrendingUp className="text-purple-600" size={20} />
      }
    ]
  }), []);

  // Progressive weekly program structure
  const weeklyProgram = useMemo(() => ({
    1: { focus: 'Mobility & Activation', emphasis: 'Establish routine, focus on stretching' },
    2: { focus: 'Mobility & Activation', emphasis: 'Increase hold times, perfect form' },
    3: { focus: 'Strength Building', emphasis: 'Add resistance, maintain mobility work' },
    4: { focus: 'Strength Building', emphasis: 'Increase sets, integrate into workouts' },
    5: { focus: 'Integration', emphasis: 'Full routine, increased load' },
    6: { focus: 'Integration', emphasis: 'Advanced progressions' },
    7: { focus: 'Integration', emphasis: 'Consolidate form; build endurance without losing posture' },
    8: { focus: 'Maintenance', emphasis: 'Assess progress, maintain gains' },
    9: { focus: 'Integration', emphasis: 'Blend mobility + strength; keep weekly consistency' },
    10: { focus: 'Integration', emphasis: 'Refine technique; add challenge while staying tall' },
    11: { focus: 'Maintenance', emphasis: 'Recovery + habit building; keep the routine sustainable' },
    12: { focus: 'Long-term Success', emphasis: 'Lifestyle integration, visible results' }
  }), []);

  // Interactive Exercise Card Component
  const ExerciseCard = ({ exercise, category }) => {
    const isExpanded = expandedExercise === exercise.id;
    const isCompleted = completedExercises[exercise.id];
    const isTimerActive = activeTimer === exercise.id;

    return (
      <div className={`card transition-all duration-300 hover:shadow-xl ${
        isCompleted ? 'border-green-400 bg-green-50' : 'border-gray-200'
      }`}>
        <div className="p-6">
          {/* Header with completion checkbox and expand button */}
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center space-x-4">
              <button
                type="button"
                onClick={() => markComplete(exercise.id)}
                aria-pressed={!!isCompleted}
                aria-label={isCompleted ? 'Mark exercise incomplete' : 'Mark exercise complete'}
                className={`w-7 h-7 rounded-full border-2 flex items-center justify-center transition-all duration-200 hover:scale-110 ${
                  isCompleted 
                    ? 'bg-green-500 border-green-500 text-white shadow-lg' 
                    : 'border-gray-300 hover:border-green-400 hover:bg-green-50'
                }`}
              >
                {isCompleted && <CheckCircle size={18} />}
              </button>
              <div className="flex items-center space-x-3">
                {exercise.icon}
                <div>
                  <h3 className="font-semibold text-gray-800 text-lg">{exercise.name}</h3>
                  <p className="text-sm text-gray-600 flex items-center">
                    <Clock size={14} className="mr-1" />
                    {exercise.frequency}
                  </p>
                </div>
              </div>
            </div>
            <button
              type="button"
              onClick={() => setExpandedExercise(isExpanded ? null : exercise.id)}
              aria-expanded={isExpanded}
              className="text-gray-500 hover:text-gray-700 transition-colors p-2 hover:bg-gray-100 rounded-lg"
            >
              {isExpanded ? <ChevronUp size={24} /> : <ChevronDown size={24} />}
            </button>
          </div>

          {/* Expanded exercise details */}
          {isExpanded && (
            <div className="space-y-4 pt-4 border-t border-gray-200 animate-slide-up">
              {/* Exercise description */}
              <div className="bg-blue-50 p-4 rounded-lg border-l-4 border-blue-400">
                <div className="flex items-start space-x-2">
                  <Lightbulb className="text-blue-600 mt-0.5 flex-shrink-0" size={16} />
                  <p className="text-sm text-gray-700 leading-relaxed">{exercise.description}</p>
                </div>
              </div>
              
              {/* Pro tips */}
              <div className="bg-yellow-50 p-4 rounded-lg border-l-4 border-yellow-400">
                <div className="flex items-start space-x-2">
                  <AlertTriangle className="text-yellow-600 mt-0.5 flex-shrink-0" size={16} />
                  <div>
                    <p className="text-sm font-medium text-yellow-800 mb-1">Pro Tip:</p>
                    <p className="text-sm text-yellow-700 leading-relaxed">{exercise.tips}</p>
                  </div>
                </div>
              </div>

              {/* Exercise illustration */}
              <div className="bg-white p-4 rounded-lg border border-gray-200">
                <img
                  src={`${imageBaseUrl}${exercise.id}.png`}
                  alt={exercise.name}
                  loading="lazy"
                  className="w-full h-44 object-contain rounded-md bg-white"
                />
              </div>

              {/* Timer and controls */}
              <div className="bg-gray-50 p-4 rounded-lg border border-gray-200">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="font-medium text-gray-800 mb-2">{exercise.reps}</p>
                    {isTimerActive && (
                      <div className="flex items-center space-x-2">
                        <Clock className="text-blue-600" size={20} />
                        <p className="text-3xl font-bold text-blue-600 font-mono">
                          {formatTime(timeRemaining)}
                        </p>
                      </div>
                    )}
                  </div>
                  <div className="flex space-x-3">
                    {!isTimerActive ? (
                      <button
                        type="button"
                        onClick={() => startTimer(exercise.id, exercise.duration)}
                        className="btn-primary flex items-center space-x-2 hover:scale-105 transition-transform"
                      >
                        <Play size={16} />
                        <span>Start</span>
                      </button>
                    ) : (
                      <>
                        <button
                          type="button"
                          onClick={pauseTimer}
                          className="bg-orange-500 hover:bg-orange-600 text-white font-medium py-2 px-4 rounded-lg transition-colors flex items-center space-x-2"
                        >
                          <Pause size={16} />
                          <span>{timerActive ? 'Pause' : 'Resume'}</span>
                        </button>
                        <button
                          type="button"
                          onClick={resetTimer}
                          className="bg-gray-500 hover:bg-gray-600 text-white font-medium py-2 px-3 rounded-lg transition-colors"
                        >
                          <RotateCcw size={16} />
                        </button>
                      </>
                    )}
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    );
  };

  // Tab Button Component
  const TabButton = ({ id, label, icon: Icon, isActive, onClick }) => (
    <button
      type="button"
      onClick={() => onClick(id)}
      className={`tab-button ${
        isActive ? 'tab-button-active' : 'tab-button-inactive'
      }`}
      aria-current={isActive ? 'page' : undefined}
    >
      <Icon size={20} />
      <span>{label}</span>
    </button>
  );

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50">
      <div className="container mx-auto px-4 py-8">
        {/* Header Section */}
        <header className="text-center mb-12">
          <h1 className="text-5xl font-bold text-gradient mb-4">Posture Correction Program</h1>
          <p className="text-gray-600 text-xl max-w-2xl mx-auto leading-relaxed">
            Transform your posture, transform your confidence. A comprehensive 12-week program designed by posture experts.
          </p>
        </header>

        {/* Navigation Tabs */}
        <nav className="flex flex-wrap justify-center gap-4 mb-12">
          <TabButton
            id="assessment"
            label="Assessment"
            icon={Target}
            isActive={activeTab === 'assessment'}
            onClick={setActiveTab}
          />
          <TabButton
            id="exercises"
            label="Exercises"
            icon={TrendingUp}
            isActive={activeTab === 'exercises'}
            onClick={setActiveTab}
          />
          <TabButton
            id="progress"
            label="Progress"
            icon={Calendar}
            isActive={activeTab === 'progress'}
            onClick={setActiveTab}
          />
        </nav>

        {/* Assessment Tab Content */}
        {activeTab === 'assessment' && (
          <div className="max-w-5xl mx-auto space-y-8">
            {/* Posture Analysis Section */}
            <div className="card p-8">
              <h2 className="text-3xl font-bold text-gray-800 mb-6 flex items-center">
                <Target className="mr-3 text-red-600" size={28} />
                Your Posture Assessment
              </h2>
              
              <div className="bg-red-50 border border-red-200 rounded-lg p-6 mb-8">
                <h3 className="font-semibold text-red-800 mb-4 text-lg flex items-center">
                  <AlertTriangle className="mr-2" size={20} />
                  Current Issues Identified:
                </h3>
                <p className="text-gray-700 leading-relaxed text-base">
                  Your posture shows <strong>forward-rounded shoulders</strong> with <strong>mild thoracic kyphosis</strong>. 
                  This pattern indicates tightness in the pectorals and upper trapezius, coupled with weakness or 
                  lengthening of the mid/lower trapezius, rhomboids, and deep neck flexors. The imbalance pulls 
                  your shoulders forward and down, causing the hunched appearance.
                </p>
              </div>
              
              {/* Muscle Analysis Grid */}
              <div className="grid md:grid-cols-2 gap-8">
                <div className="bg-blue-50 rounded-lg p-6 border-l-4 border-blue-400">
                  <h3 className="font-semibold text-blue-800 mb-4 text-lg flex items-center">
                    <Zap className="mr-2" size={20} />
                    Tight Muscles to Release:
                  </h3>
                  <ul className="space-y-3 text-gray-700">
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-blue-600 rounded-full"></div>
                      <span>Pectoralis Major & Minor</span>
                    </li>
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-blue-600 rounded-full"></div>
                      <span>Upper Trapezius</span>
                    </li>
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-blue-600 rounded-full"></div>
                      <span>Anterior Deltoid</span>
                    </li>
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-blue-600 rounded-full"></div>
                      <span>Thoracic Spine</span>
                    </li>
                  </ul>
                </div>
                
                <div className="bg-green-50 rounded-lg p-6 border-l-4 border-green-400">
                  <h3 className="font-semibold text-green-800 mb-4 text-lg flex items-center">
                    <TrendingUp className="mr-2" size={20} />
                    Weak Muscles to Strengthen:
                  </h3>
                  <ul className="space-y-3 text-gray-700">
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-green-600 rounded-full"></div>
                      <span>Mid/Lower Trapezius</span>
                    </li>
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-green-600 rounded-full"></div>
                      <span>Rhomboids</span>
                    </li>
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-green-600 rounded-full"></div>
                      <span>Deep Neck Flexors</span>
                    </li>
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-green-600 rounded-full"></div>
                      <span>Posterior Deltoid</span>
                    </li>
                  </ul>
                </div>
              </div>
            </div>

            {/* Lifestyle Adjustments Section */}
            <div className="card p-8">
              <h2 className="text-3xl font-bold text-gray-800 mb-6 flex items-center">
                <Lightbulb className="mr-3 text-yellow-600" size={28} />
                Lifestyle Adjustments
              </h2>
              <div className="grid md:grid-cols-2 gap-8">
                <div className="space-y-4">
                  <h3 className="font-semibold text-gray-800 text-lg flex items-center">
                    <Clock className="mr-2 text-blue-600" size={20} />
                    Daily Habits:
                  </h3>
                  <ul className="space-y-3 text-gray-700">
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-blue-600 rounded-full"></div>
                      <span>Reset posture every 30 minutes</span>
                    </li>
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-blue-600 rounded-full"></div>
                      <span>Position monitor at eye level</span>
                    </li>
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-blue-600 rounded-full"></div>
                      <span>Use ergonomic workstation setup</span>
                    </li>
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-blue-600 rounded-full"></div>
                      <span>Take regular movement breaks</span>
                    </li>
                  </ul>
                </div>
                
                <div className="space-y-4">
                  <h3 className="font-semibold text-gray-800 text-lg flex items-center">
                    <Heart className="mr-2 text-red-600" size={20} />
                    Sleep Setup:
                  </h3>
                  <ul className="space-y-3 text-gray-700">
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-red-600 rounded-full"></div>
                      <span>Use single medium pillow</span>
                    </li>
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-red-600 rounded-full"></div>
                      <span>Avoid forward neck flexion</span>
                    </li>
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-red-600 rounded-full"></div>
                      <span>Consider cervical pillow</span>
                    </li>
                    <li className="flex items-center space-x-2">
                      <div className="w-2 h-2 bg-red-600 rounded-full"></div>
                      <span>Sleep on back or side</span>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Exercises Tab Content */}
        {activeTab === 'exercises' && (
          <div className="max-w-7xl mx-auto space-y-8">
            {/* Weekly Program Header */}
            <div className="card p-6">
              <h2 className="text-3xl font-bold text-gray-800 mb-6 flex items-center">
                <Calendar className="mr-3 text-blue-600" size={28} />
                Today's Workout Plan
              </h2>
              <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between space-y-4 lg:space-y-0">
                <div className="flex items-center space-x-6">
                  <div className="bg-blue-100 px-4 py-2 rounded-full">
                    <span className="text-blue-800 font-semibold text-lg">Week {currentWeek}</span>
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-800 text-xl">{weeklyProgram[currentWeek]?.focus}</h3>
                    <p className="text-gray-600">{weeklyProgram[currentWeek]?.emphasis}</p>
                  </div>
                </div>
                <div className="flex space-x-3">
                  <button
                    onClick={() => setCurrentWeek(Math.max(1, currentWeek - 1))}
                    className="btn-secondary"
                  >
                    ← Prev Week
                  </button>
                  <button
                    onClick={() => setCurrentWeek(Math.min(12, currentWeek + 1))}
                    className="btn-secondary"
                  >
                    Next Week →
                  </button>
                </div>
              </div>
            </div>

            {/* Exercise Categories */}
            <div className="space-y-10">
              {/* Mobility & Stretching */}
              <div>
                <h3 className="text-2xl font-semibold text-gray-800 mb-6 flex items-center">
                  <Clock className="mr-3 text-blue-600" size={28} />
                  Mobility & Stretching (Daily)
                </h3>
                <div className="grid gap-6">
                  {exercises.mobility.map((exercise) => (
                    <ExerciseCard key={exercise.id} exercise={exercise} category="mobility" />
                  ))}
                </div>
              </div>

              {/* Strengthening */}
              <div>
                <h3 className="text-2xl font-semibold text-gray-800 mb-6 flex items-center">
                  <TrendingUp className="mr-3 text-green-600" size={28} />
                  Strengthening (3-4x/week)
                </h3>
                <div className="grid gap-6">
                  {exercises.strengthening.map((exercise) => (
                    <ExerciseCard key={exercise.id} exercise={exercise} category="strengthening" />
                  ))}
                </div>
              </div>

              {/* Neck & Spinal Alignment */}
              <div>
                <h3 className="text-2xl font-semibold text-gray-800 mb-6 flex items-center">
                  <Target className="mr-3 text-purple-600" size={28} />
                  Neck & Spinal Alignment (Daily)
                </h3>
                <div className="grid gap-6">
                  {exercises.alignment.map((exercise) => (
                    <ExerciseCard key={exercise.id} exercise={exercise} category="alignment" />
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Progress Tab Content */}
        {activeTab === 'progress' && (
          <div className="max-w-5xl mx-auto space-y-8">
            <div className="card p-8">
              <h2 className="text-3xl font-bold text-gray-800 mb-8 flex items-center">
                <TrendingUp className="mr-3 text-green-600" size={28} />
                Your Progress Journey
              </h2>
              
              {/* Progress Metrics */}
              <div className="grid md:grid-cols-3 gap-6 mb-10">
                <div className="bg-blue-50 rounded-lg p-6 text-center border-l-4 border-blue-400">
                  <div className="text-4xl font-bold text-blue-600 mb-3">
                    {Object.values(completedExercises).filter(Boolean).length}
                  </div>
                  <p className="text-gray-600 font-medium">Exercises Completed Today</p>
                </div>
                
                <div className="bg-green-50 rounded-lg p-6 text-center border-l-4 border-green-400">
                  <div className="text-4xl font-bold text-green-600 mb-3">Week {currentWeek}</div>
                  <p className="text-gray-600 font-medium">Current Program Week</p>
                </div>
                
                <div className="bg-purple-50 rounded-lg p-6 text-center border-l-4 border-purple-400">
                  <div className="text-4xl font-bold text-purple-600 mb-3">
                    {Math.round((currentWeek / 12) * 100)}%
                  </div>
                  <p className="text-gray-600 font-medium">Program Completion</p>
                </div>
              </div>

              {/* Timeline & Milestones */}
              <div className="mb-10">
                <h3 className="text-xl font-semibold text-gray-800 mb-6">Timeline & Milestones</h3>
                <div className="space-y-4">
                  {Array.from({ length: 12 }, (_, i) => i + 1).map((week) => {
                    const data = weeklyProgram[week];
                    const isActive = currentWeek >= week;
                    return (
                      <div
                        key={week}
                        className={`flex items-center p-5 rounded-lg border-2 transition-all duration-300 hover:shadow-md ${
                          isActive
                            ? 'border-green-400 bg-green-50 shadow-sm'
                            : 'border-gray-200 bg-gray-50'
                        }`}
                      >
                        <div
                          className={`w-10 h-10 rounded-full flex items-center justify-center mr-5 transition-all duration-300 ${
                            isActive
                              ? 'bg-green-500 text-white shadow-lg scale-110'
                              : 'bg-gray-300 text-gray-600'
                          }`}
                        >
                          {isActive ? <CheckCircle size={20} /> : week}
                        </div>
                        <div>
                          <h4 className="font-semibold text-gray-800 text-lg">
                            Week {week}: {data.focus}
                          </h4>
                          <p className="text-gray-600">{data.emphasis}</p>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>

              {/* Expected Results */}
              <div className="bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg p-8 text-white">
                <h3 className="text-2xl font-semibold mb-4 flex items-center">
                  <Target className="mr-3" size={24} />
                  Expected Results
                </h3>
                <p className="mb-4 text-lg leading-relaxed">
                  By 8-12 weeks: Visible reduction in rounded shoulders and improved upright posture at rest.
                </p>
                <div className="text-sm opacity-90 leading-relaxed">
                  Remember: Consistency is key. Small daily efforts compound into significant long-term changes.
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default PostureCorrectionApp;
