import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import react from 'eslint-plugin-react'
export default [ {ignores:['dist']}, js.configs.recommended, {files:['**/*.{js,jsx}'],languageOptions:{ecmaVersion:2022,globals:{...globals.browser,...globals.node},parserOptions:{ecmaVersion:'latest',sourceType:'module',ecmaFeatures:{jsx:true}}},plugins:{react,'react-hooks':reactHooks,'react-refresh':reactRefresh},rules:{...reactHooks.configs.recommended.rules,'no-empty':['error',{allowEmptyCatch:true}],'react/jsx-uses-vars':'error','react-refresh/only-export-components':['warn',{allowConstantExport:true}]}} ]
