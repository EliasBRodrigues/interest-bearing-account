import { useEffect, useRef, useState } from "react";
import { Dialog } from "primereact/dialog";
import { InputText } from "primereact/inputtext";
import { Button } from "primereact/button";
import { Message } from "primereact/message";
import { api } from "../../utils/api";

export default function MovementDialog({ kind, session, onClose, onSuccess }) {
  const 
    [value, setValue] = useState(""),
    [busy, setBusy] = useState(false),
    [error, setError] = useState("");

  const guard = useRef(false),
    input = useRef(null);

  useEffect(() => {
    input.current?.focus();
  }, []);
  
  const deposit = kind === "depositos";
  
  async function submit(event) {
    event.preventDefault();
    // Bloqueia inclusive dois eventos antes do próximo render.
    if (guard.current) return;
    const normalized = value.trim().replace(",", ".");
    if (!/^\d+(\.\d{1,2})?$/.test(normalized)) {
      setError(
        "Informe o valor com até duas casas decimais, por exemplo 150,00.",
      );
      return;
    }
    guard.current = true;
    setBusy(true);
    setError("");
  
    try {
      await api.move(session, kind, normalized);
      onSuccess();
    } catch (e) {
      setError(e.message);
    } finally {
      guard.current = false;
      setBusy(false);
    }
  }
  
  return (
    <Dialog
      header={deposit ? "Fazer um depósito" : "Fazer um saque"}
      visible
      modal
      draggable={false}
      closable={!busy}
      closeOnEscape={!busy}
      onHide={() => {
        if (!busy) onClose();
      }}
      className="movement-dialog"
    >
      <form onSubmit={submit} className="form-stack">
        <p className="muted">
          {deposit
            ? "O valor será creditado na sua conta."
            : "O valor será debitado do seu saldo disponível."}
        </p>
        <label htmlFor="movement-value">Valor em reais</label>
        <div className="money-input">
          <span>R$</span>
          <InputText
            ref={input}
            id="movement-value"
            inputMode="decimal"
            autoComplete="off"
            placeholder="0,00"
            value={value}
            onChange={(e) => setValue(e.target.value)}
            disabled={busy}
            required
            maxLength={22}
          />
        </div>
        {error && (
          <div role="alert">
            <Message severity="error" text={error} />
          </div>
        )}
        <div className="dialog-actions">
          <Button
            label="Cancelar"
            type="button"
            outlined
            severity="secondary"
            onClick={onClose}
            disabled={busy}
          />
          <Button
            label={deposit ? "Confirmar depósito" : "Confirmar saque"}
            type="submit"
            loading={busy}
          />
        </div>
      </form>
    </Dialog>
  );
}
