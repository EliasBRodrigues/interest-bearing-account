import { useRef, useState } from "react";
import { InputText } from "primereact/inputtext";
import { Password } from "primereact/password";
import { Button } from "primereact/button";
import { Message } from "primereact/message";
import { api } from "../utils/api";

export default function AuthForm({ onLogin, notice }) {
  const [register, setRegister] = useState(false),
    [number, setNumber] = useState(""),
    [owner, setOwner] = useState(""),
    [password, setPassword] = useState("");
  const [busy, setBusy] = useState(false),
    [error, setError] = useState(""),
    [created, setCreated] = useState("");
  const guard = useRef(false);
  function switchMode() {
    setRegister((v) => !v);
    setError("");
    setCreated("");
    setPassword("");
  }
  async function submit(event) {
    event.preventDefault();
    if (guard.current) return;
    guard.current = true;
    setBusy(true);
    setError("");
    let accountNumber = number;
    try {
      if (register) {
        const result = await api.create({
          titular: owner.trim(),
          senha: password,
        });
        accountNumber = result.numero;
        setCreated(result.numero);
        setNumber(result.numero);
        setRegister(false);
      }
      const session = await api.login({
        numero: accountNumber,
        senha: password,
      });
      onLogin(session, register ? accountNumber : null);
    } catch (e) {
      setError(e.message);
    } finally {
      setBusy(false);
      guard.current = false;
    }
  }
  return (
    <section className="auth-card" aria-labelledby="auth-title">
      <div className="eyebrow">
        {register ? "SEU PRÓXIMO PASSO" : "BEM-VINDO DE VOLTA"}
      </div>
      <h2 id="auth-title">
        {register ? "Abra sua conta" : "Entre na sua conta"}
      </h2>
      {notice && (
        <div role="alert">
          <Message severity="warn" text={notice} />
        </div>
      )}
      {created && (
        <div role="status">
          <Message
            severity="success"
            text={
              "Conta " +
              created +
              " criada. Entre com sua senha para continuar."
            }
          />
        </div>
      )}
      <form onSubmit={submit} className="form-stack">
        {register ? (
          <>
            <label htmlFor="owner">Nome do titular</label>
            <InputText
              id="owner"
              value={owner}
              onChange={(e) => setOwner(e.target.value)}
              maxLength={120}
              autoComplete="name"
              placeholder="Seu nome completo"
              required
              disabled={busy}
            />
          </>
        ) : (
          <>
            <label htmlFor="number">Número da conta</label>
            <InputText
              id="number"
              value={number}
              onChange={(e) => setNumber(e.target.value)}
              autoComplete="username"
              inputMode="numeric"
              placeholder="Ex.: 100001"
              required
              disabled={busy}
            />
          </>
        )}
        <label htmlFor="password">Senha</label>
        <Password
          inputId="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          toggleMask
          feedback={false}
          autoComplete={register ? "new-password" : "current-password"}
          placeholder={register ? "No mínimo 6 caracteres" : "Sua senha"}
          required
          minLength={register ? 6 : undefined}
          disabled={busy}
        />
        {register && (
          <small className="muted">
            Guarde o número gerado ao concluir o cadastro.
          </small>
        )}
        {error && (
          <div role="alert">
            <Message severity="error" text={error} />
          </div>
        )}
        <Button
          label={register ? "Criar minha conta" : "Entrar"}
          type="submit"
          loading={busy}
          className="full-width"
        />
      </form>
      <div className="auth-switch">
        {register ? "Já tem uma conta?" : "Ainda não tem uma conta?"}{" "}
        <button type="button" onClick={switchMode} disabled={busy}>
          {register ? "Entrar" : "Abrir conta"}
        </button>
      </div>
    </section>
  );
}
