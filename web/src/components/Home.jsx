import { useEffect, useState } from "react";
import AuthForm from "./auth/AuthForm";
import Dashboard from "./screens/Dashboard";

export default function Home() {
  function storedSession() {
    try {
      const value = JSON.parse(sessionStorage.getItem("rende-session"));
      return value?.token && value?.contaId ? value : null;
    } catch {
      return null;
    }
  }

  function logout() {
    sessionStorage.removeItem("rende-session");
    setSession(null);
    setNotice("");
    setCreatedNumber(null);
  }

  const [session, setSession] = useState(storedSession),
    [notice, setNotice] = useState(""),
    [createdNumber, setCreatedNumber] = useState(null);

  
  useEffect(() => {
      function expired() {
        sessionStorage.removeItem("rende-session");
        setSession(null);
        setCreatedNumber(null);
        setNotice("Sua sessão expirou. Entre novamente para continuar.");
      }
      window.addEventListener("session-expired", expired);
      return () => window.removeEventListener("session-expired", expired);
    }, []);

  function login(next, number) {
    sessionStorage.setItem("rende-session", JSON.stringify(next));
    setSession(next);
    setNotice("");
    setCreatedNumber(number);
  }

    if (session)
      return (
        <Dashboard
          session={session}
          onLogout={logout}
          createdNumber={createdNumber}
        />
      );

  return (
    <div className="auth-page">
      <div className="auth-shell">
        <section className="auth-intro">
          <div className="intro-copy">
            <div className="intro-pill">
              <i className="pi pi-sparkles" /> Login
            </div>
            <h1>Conta Remunerada</h1>
            <p>Desafio Conta Remunerada</p>
          </div>
        </section>
        <div className="auth-form-area">
          <AuthForm onLogin={login} notice={notice} />
        </div>
      </div>
    </div>
  );
}
