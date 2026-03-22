Instagram Poster

An automated system that generates engaging captions using AI and posts images to Instagram at scheduled times — built with Spring Boot + Groq + Cloudinary + Instagram Graph API.

⸻

✨ Features
	•	📸 Upload images from local storage
	•	🤖 Generate captions using AI (Groq / LLaMA 3)
	•	🎯 Optimized captions with hashtags & engagement hooks
	•	🖼️ Automatic image transformation (Instagram-friendly aspect ratio)
	•	☁️ Upload images to Cloudinary (public HTTPS URL)
	•	📅 Scheduled posting using cron jobs
	•	📤 Publish posts via Instagram Graph API

🧠 How It Works

Local Image
   ↓
Cloudinary Upload (public URL + transformation)
   ↓
AI Caption Generation (Groq)
   ↓
Instagram Graph API
   ↓
Post Published 🚀

🛠️ Tech Stack
	•	Backend: Java, Spring Boot
	•	AI: Groq (LLaMA 3)
	•	Media Hosting: Cloudinary
	•	APIs: Instagram Graph API (via Meta)
	•	Scheduler: Spring @Scheduled (cron-based)
